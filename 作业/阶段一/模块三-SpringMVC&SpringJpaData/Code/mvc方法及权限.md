/demo:是在方法上添加@Security注解

| 用户     | /demo/query | /demo/queryByUser | /demo/queryByUsers |
| -------- | ----------- | ----------------- | ------------------ |
| 任何人   | 有          | --                | --                 |
| zhangsan | 有          | --                | 有                 |
| lisi     | 有          | 有                | 有                 |
| wangwu   | 有          | --                | --                 |



/security:是在controller和method上添加@Security注解

| 用户     | /security/query | /security/queryByUser | /security/queryByUsers |
| -------- | --------------- | --------------------- | ---------------------- |
| 任何人   | --              | --                    | --                     |
| zhangsan | 有              | --                    | 有                     |
| lisi     | 有              | 有                    | 有                     |
| wangwu   | --              | --                    | --                     |

