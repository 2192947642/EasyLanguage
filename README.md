### 介绍

EasyLanguag是一个运行于java环境的嵌入脚本语言，有类似于JavaScript的语法



### 作用域

- 块作用域
- 函数作用域
- 类作用域
- 静态类作用域
- 全局作用域

### 使用方法

```java
package EasyLanguage;
import EasyLanguage.exceptions.ParseException;
public class Main {

    private static class Test{
        public static Integer InitFunc(Integer val){
            return val;
        }
        int a=5;
        public void addNumber(Integer b){
            a+=b;
        }
        public void getName(){
            System.out.println("a");
        }
    }
    public static void sayHello(){

    }

    public static void main(String[] args) throws ParseException {
        EasyEnv env=EasyEnv.buildWithInjectMap();//创建一个运行环境
        InjectMap injectMap=env.getInjectMap();//获得注入map
        Test test=new Test();
        injectMap.putVal("init",test);//传入一个值
        injectMap.putFunc("initFunc",Test.class,"InitFunc",Integer.class);//注入			一个方法
        //运行代码
        String code= """
                init.getName();
                let a=10;
                print(a);
                """;
        env.setRunCode(code);
        //
        env.run();
    }

}
```



### 基本语法

##### 定义变量

```javascript
let a=10;//定义一个变量
a="123456";
let b=a;
b+=1;//注意 ++ 和 --均未进行实现
b-=1;
//变量在定义时必须进行初始化 let a;这样的格式并不会被允许
//进行类型转换
b=toInt(b);
b=toLong(b);
b=toDouble(b);
b=toFloat(b);
//因为变量是java中的类型 所以在Java中的String类型或者Integer类型等的方法都可以进行调用
//例如当b为string类型时可以打印b的长度
b="1234";
print(b.length());
//数组类型是以arrayList为基础进行实现的，所以arraylist的方法都可以进行调用
b=[];//定义一个空数组
b=[1,2,3,4]//定义一个具有初始数据的空数组
print(b.size());//打印数组的长度
b.add(5);//新增一个数据
```

##### 流程控制

```javascript
let b=2;
if(b==2){
	print("equal");
}; //if 语句 必须以;进行结尾 // elif语句也必须以;进行结尾
if(b<2){
	print("less");
};
if(b<2){
 print("less")
}else{
  print(666);
};
if(b<2){ 
	print("less");
}elif(b>2){
	print("more");
}else{
	print("equal")；
}
```

##### 循环

```javascript
let a=10;
while(a>1){
	a-=1;
}
do{
    if(a==2){
		a+=2;
        continue;
    }
	a+=1;
    if(a==8){
        break;
    }
   
}while(a<10)
for(a=10;a<20;a+=1){
	print(a)
}
//注意类似于for(;;)这样的写法是不被允许的 三个位置都必须有表达式
```



##### 定义函数

```javascript
function sayHello(){
	print(5);
}
sayHello();//同名函数只能存在一个，参数个数不可以超过最大接受的参数长度
function add(a,b){
	print(a+b);
}
let b=$()=>{//同样为定义方法
    print(1234);
}
b();
add(1,2);//结果为3.0
add(1); //结果为1.0undefined 此时1.0转换为字符串类型 与 undefined进行相加
```

##### 定义类

```javascript
class Hello{
    static let a=17;//类的静态属性
    static function sayHello(){ //静态方法
    	print("staticHello");
    }
	static let sayHi=$()=>{
        print("Hi");
    }
    static{
    	a+=1;//类的静态初始化代码块
    	print(a);
    }
    function sayHello(){
    	print(this.a);
    	print("instanteHello")
    }
	let a=10;//实体类的属性
	let b=10;
	let c=1;
}
let hello=Hello.new();//实例化类,类内的属性可以任意的增加
hello.d=5;
print(hello.d);

class Con{
	let a=10;
	constructor(b){//构造方法 有且只有一个
		a+=b;
		print(a);
	}
}

let con=Con.new(2);
//继承
class Animal{
    let animal="animal:";
	constructor(){
        print("animalCon");
    }
	function sing(){
        print("animal");
    }
}
class Dog extends Animal{
    let animal="dog";

	function sing(){
        super.sing();
    }
	function detail(){
        print(super.animal+this.animal);
    }
}
let dog=Dog.new(); //此时会输出 animalCon

class Cat extends Animal{
    constructor(){
        super.constructor();//当显式定义构造方法时 需要手动调用父类的构造方法
    }
}
```

