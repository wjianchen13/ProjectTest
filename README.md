# 项目相关测试
1.

这是什么意思
    public PayCoreImpl() {
        CoreManager.addClient(this);
    }




# 遇到问题
1. 创建新项目，编译报错
Can't determine type for tag '<macro name="m3_comp_assist_chip_container_shape">?attr/shapeAppearanceCornerSmall</macro>'
解决方法：
降低引入库的版本号
```Groovy
implementation 'androidx.appcompat:appcompat:1.6.0'
implementation 'com.google.android.material:material:1.8.0'
```
改为
```Groovy
implementation 'androidx.appcompat:appcompat:1.4.1'
implementation 'com.google.android.material:material:1.6.0'
```