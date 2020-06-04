package com.study.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lagou_auth_code")
public class AuthCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 邮箱地址
    private String email;

    // 验证码
    private String code;

    // 创建时间
    @Column(name = "createtime")
    private Date createTime;

    // 过期时间
    @Column(name = "expiretime")
    private Date expireTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
