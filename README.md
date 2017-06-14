# Anna

Anna是由美柚出品的一个小巧、方便但功能强大的Android AOP库。

## 什么是Anna

它能帮助你将原本的一个类或者一个方法甚至一个属性进行修改、替换、拦截、插入，实现Java级别的开放插桩AOP。

Anna通过编译期的插桩替换操作来达到dex修改的能力。

通过Anna，你可以实现热补丁、无痕埋点等一系列在Java运行期无法完成的工作,并且非侵入。

## 可以做什么

无痕埋点、热补丁、自动化计算耗时、插桩等等都可以通过Anna方便实现。

## 使用方法

### 添加插件

在主工程的build.gradle中加入

```groovy
classpath 'com.meiyou.sdk.plugin:anna-compiler:0.0.82-SNAPSHOT'
```

在你的主工程中加入插件和依赖

```groovy
apply plugin: 'anna'


compile 'com.meiyou.sdk.lib:anna:0.0.12-SNAPSHOT'
```

### 配置处理的方法

在主工程根目录创建文件"anna_list.pro",书写内容如下:

```groovy
#插入模式
-switch {
 all;
# custom;
}

#包含
-include {
  ** on[C]lick+;
  com.meetyou.demo onCreate+;
}
```

switch表示的是当前AOP的情况,all表示所有方法都AOP,custom表示自定义,使用custom之后,Anna会从include中选择AOP的方法,all则不会走include。

include内表示需要AOP的类和方法。

第一个参数表示AOP的类,第二个为正则表达式,表示需要AOP的方法。

**表示全部,不区分类,所有的类都会AOP。

上面表示所有只要匹配on[C]lick+正则的方法都会被AOP,以及com.meetyou.demo的onCreate+正则的方法也会被AOP。

### 方法接收器

实现了AOP之后,我们需要对方法进行控制操作。

随便找个地方写个类，继承IAnnaReceiver类,给这个类标上注解@AnnaReceiver("** on[C]reate+")。

注解AnnaReceiver表示该类为接收器,内容** on[C]reate+与配置文件的意思相符,用法一一致。

```java
@AnnaReceiver("** on[C]reate+")
public class TestReceiver extends IAnnaReceiver{
    @Override
    public boolean onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype) {
        Log.d("TestReceiver", "onMethodEnter:" + clazz + ";" + name);
        return super.onMethodEnter(clazz, obj, name, objects, rtype);
    }

    @Override
    public void onMethodExit(String clazz, Object obj, String name, String rtype) {
        super.onMethodExit(clazz, obj, name, rtype);
        Log.d("TestReceiver", "onMethodExit:" + clazz + ";" + name);
    }

    @Override
    public Object onIntercept(String clazz, Object obj, String name, Object[] objects, String rtype) {
        Log.d("TestReceiver", "onIntercept:" + clazz + ";" + name);
        return super.onIntercept(clazz, obj, name, objects, rtype);
    }
}

```

### 参数说明

IAnnaReceiver有几个方法,每个方法都有固定的参数:
* ```String clazz```: 方法执行所在的类
* ```Object obj```: 方法被执行所在对象,null表示当前是静态方法
* ```String name```:被执行的方法名
* ```Object[] objects```:方法参数
* ```String rtype```:方法返回值类型

### onMethodEnter

onMethodEnter表示监听的方法被执行了。

onMethodEnter需要boolean类型的返回值,表示是否替换该方法。

替换的意思为接下去原方法不再执行,执行的是Receiver内的方法。

返回true表示替换,false为不替换。默认为false返回。

onMethodEnter是一个方法必然会执行的生命周期。

### onMethodExit

表示该原方法已经执行完毕,该方法会受到onMethodEnter的返回值的影响,如果onMethodEnter返回true,表示替换情况下onMethodExit是不会被执行的。

### onIntercept

表示拦截替换的操作,该方法只会在onMethodEnter返回true的时候被执行,该方法需要返回object类型对象,如果被替换的方法需要返回值,这里的object会被原方法的返回所使用。


## 拓展注解

* ```@AntiAnna```注解的意思是该类不允许被AOP,一些基础的Anna文件都会有这个注解。

## 完成编译

配置和操作完毕后,重新编译或者运行就可以发现你的代码被插入了

## 逻辑图

### 替换方法Replace

虚线为原本执行的方法路线，实线为Anna之后的方法路线。

![p1](https://raw.githubusercontent.com/HomHomLin/Anna/master/replace.png)

### 插入方法Insert

虚线为原本执行的方法路线，实线为Anna之后的方法路线。

![p2](https://raw.githubusercontent.com/HomHomLin/Anna/master/insert.png)

## 生命周期示意

![p2](https://raw.githubusercontent.com/HomHomLin/Anna/master/liucheng.png)


## Developed By

 * Linhonghong - <QQ:371655539，mail:371655539@qq.com>

## Attention
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
