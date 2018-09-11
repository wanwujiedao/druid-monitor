package com.wwjd.druidmonitor.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 青团推手用户参与
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @created 2018年08月07日 09:25:00
 * @Modified_By 阿导 2018/8/7 09:25
 */
@Table(name = "ts_user")
public class TsUser implements Serializable {
  /**
   * 自动生成的 UUID
   */
  private static final long serialVersionUID = 2960686525646351411L;
  /**
   * 主键
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`id`")
  private Long id;
  /**
   * 活动 ID
   */
  @Column(name = "`activity_id`")
  private Long activityId;
  /**
   * 用户 ID
   */
  @Column(name = "`user_id`")
  private Long userId;
  /**
   * 账户 ID
   */
  @Column(name = "`account_id`")
  private Long accountId;
  /**
   * 用户名
   */
  @Column(name = "`user_name`")
  private String userName;
  /**
   * 用户图像
   */
  @Column(name = "`head_img`")
  private String headImg;
  /**
   * 创建时间
   */
  @Column(name = "`create_time`")
  private Date createTime;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Long getActivityId() {
    return activityId;
  }
  
  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }
  
  public Long getUserId() {
    return userId;
  }
  
  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  public Long getAccountId() {
    return accountId;
  }
  
  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }
  
  public String getUserName() {
    return userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public String getHeadImg() {
    return headImg;
  }
  
  public void setHeadImg(String headImg) {
    this.headImg = headImg;
  }
  
  public Date getCreateTime() {
    return createTime;
  }
  
  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }
}
