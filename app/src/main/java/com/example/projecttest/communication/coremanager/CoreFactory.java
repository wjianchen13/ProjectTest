/**
 * Core对象工厂。使用getCore前需要注册对应core接口实现类
 * 此类是非线程安全的，必须在主线程调用
 */
package com.example.projecttest.communication.coremanager;


import java.util.HashMap;

/**
 * @author daixiang
 *
 */
public class CoreFactory {

	public  static final HashMap<Class<? extends IBaseCore>, IBaseCore> cores;
	public  static final HashMap<Class<? extends IBaseCore>, Class<? extends AbstractBaseCore>> coreClasses;

	static {
		cores = new HashMap<>();
		coreClasses = new HashMap<>();
	}

	
	/**
	 * 从工厂获取实现cls接口的对象实例
	 * 该实例是使用registerCoreClass注册的实现类的对象
	 * 
	 * @param cls 必须是core接口类，不能是core实现类，否则会抛出异常
	 * @return 如果生成对象失败，返回null
	 */
	public static <T extends IBaseCore> T getCore(Class<T> cls) {
		
		if (cls == null) {
			return null;
		}
		try {
			IBaseCore core = cores.get(cls);
			if (core == null) {
				Class<? extends AbstractBaseCore> implClass =  coreClasses.get(cls);
				if (implClass == null) {
					if (cls.isInterface()) {
						throw new IllegalArgumentException("No registered core class for: " + cls.getName());
					} else {
						throw new IllegalArgumentException("Not interface core class for: " + cls.getName());
					}
				} else {
					core = implClass.newInstance();
				}

				if (core != null) {
					cores.put(cls, core);
					//MLog.debug("CoreFactory", cls.getName() + " created: "
					//        + ((implClass != null) ? implClass.getName() : cls.getName()));
				}
			}
			return (T)core;
		} catch (Throwable e) {
		}
		return null;
	}
	
	/**
	 * 注册某个接口实现类
	 * @param coreInterface
	 * @param coreClass
	 */
	public static void registerCoreClass(Class<? extends IBaseCore> coreInterface, Class<? extends AbstractBaseCore> coreClass) {
		
		if (coreInterface == null || coreClass == null) {
			return;
		}

		coreClasses.put(coreInterface, coreClass);
	}
	
	/**
	 * 返回某个接口是否有注册实现类
	 * @param coreInterface
	 * @return
	 */
	public static boolean hasRegisteredCoreClass(Class<? extends IBaseCore> coreInterface) {
		if (coreInterface == null) {
			return false;
		} else {
		    return  coreClasses.containsKey(coreInterface);
		}
	}
}
