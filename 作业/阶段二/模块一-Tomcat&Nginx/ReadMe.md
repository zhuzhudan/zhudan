1、该项目可进行多端口监控

2、要运行，请先修改server.xml中 Host的appBase的绝对路径，可使用提交的webapps文件夹；如果要配置虚拟路径，请修改Context的docBase路径，docBase是虚拟路径中文件夹的绝对路径，当前只能配置html文件，也可使用提交的staticResources文件夹

3、如果要修改webaapps的内容，请保持web.xml路径：webapps/项目名/WEB-INF/web.xml；classes文件夹路径：webapps/项目名/WEB-INF/classes；classes中的文件需要在项目中编译后再提取出，因为要继承minicat中的HttpServlet

可使用的链接地址：

动态资源请求：

http://localhost:8080/demo1/study、http://localhost:8009/demo1/study、http://localhost:8080/demo2/resume/addresume、http://localhost:8009/demo2/resume/addresume

静态资源请求：

http://localhost:8080/demo1/index.html、http://localhost:8009/demo1/index.html、http://localhost:8080/demo3/index.html、http://localhost:8009/demo3/index.html



