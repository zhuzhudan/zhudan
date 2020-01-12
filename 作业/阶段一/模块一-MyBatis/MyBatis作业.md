1、Mybatis动态sql是做什么的？都有哪些动态sql？简述一下动态sql的执行原理？

1. 动态sql是业务逻辑复杂时，可根据不同条件拼接 SQL 语句

2. 动态sql包含：

1). if：提供一种可选的功能，只有当if中test的表达式成立，则拼接if标签中的内容

2). foreach：循环执行sql拼接，对集合（List、Set、Map、数组）进行遍历，通常是在构建 IN 条件语句的时候

3). sql：将重复的sql进行提取，使用时用include引用，达到重用sql的目的

4). choose（when，otherwise）：当提供条件的可选条件有多个，但只想从中选择其中的一个。只有when中的test表达式成立，才会拼接when中的内容，如果都不符合则使用otherwise标签中的内容

5). where：只会在至少一个子元素的条件返回sql子句的情况下才会插入“where”子句，且若语句的开头为“and”或“or”，where元素也会将他们去除

3. 首先加载配置文件，将主配置文件内容解析封装到Configuration中，将sql的配置信息加载到Configuration中的Map<String, Mappedstatement>对象，并创建DefaultSqlSessionFactory对象。

   根据参数类型创建指定类型的执行器Executor和SqlSession。根据传入的全限定名+方法名从映射的Map中取出相应MappedStatement对象，通过Executor中的方法处理JDBC的Statement的交互：首先根据传入的参数解析MappedStatement对象，动态获得SQL语句、参数，封装成BoundSql并返回，并为此次操作创建Key，如果在缓存中查询到直接返回，如果没有，则从数据库中查询，再放入缓存中。

   通过执行器的doQuery方法进行数据库查询，首先从连接池中获取连接，根据传入的参数创建StatementHandler对象，创建过程中，使用SQL语句自负串包含多个？占位符，再通过ParameterHandler对象完成对Statement对象的？占位符进行赋值。通过Statement进行数据库查询，并将返回结果resultSet交给ResultSetHandler处理，根据MappedStatement对象的结果映射配置对得到的执行结果进行转换处理，并得到最终的处理结果。

   

2、Mybatis是否支持延迟加载？如果支持，它的实现原理是什么？

Mybatis支持延迟加载

延迟加载是在进行表的关联查询时，推迟对关联对象的select查询。只有当需要关联对象时，再发出sql宇航局进行查询，可以减少数据库压力。

延迟加载主要是通过动态代理的形式实现，通过代理拦截到指定方法，执行数据加载。在ResultSetHandler中的创建结果对象时会对结果是否需要延迟加载进行判断，如果是，则创建延迟加载的代理对象。在调用代理对象的相关方法时触发二次查询



3、Mybatis都有哪些Executor执行器？它们之间的区别是什么？

Simple执行器：普通执行器，使用statement后会进行关闭

Reuse执行器：会重用预处理语句，将statement放到map中管理，下次执行会从map中获取

Batch执行器：重用语句并执行批量更新，是对PrepareStatement.addBatch方法的封装，除了支持statement的重用之外，还可批量的执行sql语句



4、简述下Mybatis的一级、二级缓存（分别从存储结构、范围、失效场景。三个方面来作答）？

一级缓存：作用范围是会话级别SqlSession，在SqlSession中用HashMap来存储缓存数据，不同的SqlSession之间的缓存数据的HashMap不影响。一级缓存默认开启，进行数据的添加、修改、删除并提交事务，或者手动刷新缓存，都会令缓存失效。

二级缓存：作用范围是Mapper的namespace，同一个namespace不同的mapper、不同的会话之间也可以共享数据，是跨SqlSession的。二级缓存的底层数据结构也是HashMap，但是不同于一级缓存，二级缓存是缓存的数据而不是对象，所以需要实体类实现Serializable接口。二级缓存需要手动配置开启，进行数据的添加、修改、删除并提交事务会令缓存失效，如果在statement的配置中使用userCache=false禁用缓存，配置flashCache=true表示刷新缓存



5、简述Mybatis的插件运行原理，以及如何编写一个插件？

插件运行原理：运用动态代理模式和责任链模式

1. 每个创建出来的对象不是直接返回，而是返回interceptorChain.pluginAll
2. 获取到所有的Interceptor，调用interceptor.plugin(target)，返回target包装后的对象
3. 使用插件为目标对象创建一个代理对象，通过AOP，代理对象可拦截四大对象的每一个执行
4. Excutor是创建SqlSession过程中，其余（ParameterHandler、ResultSetHandler、StatementHandler）都是在执行sql时，使用Plugin创建代理对象，在被拦截对象的方法调用时，调用拦截器链中的拦截器依次对目标进作业
5. 拦截或增强，先走到Plugin的invoke()方法，再走到Interceptor实现类的intercept()方法，最后通过Invocation.proceed()方法调用被拦截对象的原方法

编写插件：插件类要实现Interceptor接口，并实现Intercept方法(插件的核心方法)，plugin方法(把拦截器生成一个代理放到拦截器链中)，setProperties方法(传递插件所需参数)，并指定想要拦截的方法签名