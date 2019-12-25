# 客服系统（contact-center）

## 简介
本项目为九州饭斗,五星棋牌等APP端提供内嵌的客服咨询页面及对应的后端接口,自动回复的支付信息,以及客服角色管理等功能

## 技术简介
###前端技术
- jQuery+LayUI+KindEditor.

###后端技术
- Spring Boot 1.5.6 + Spring MVC + Spring + Spring Data JPA  + Swagger 2.2.2
- FreeMarker 2.3.25 // 前端页面模板渲染
- Hazelcast 3.10.5 // 分布式缓存,用于存储项目中经常用到的Java对象

## 项目核心类&&方法
### com.chatopera.cc.app.handler.LoginController // 登录相关
- login // 登录接口
```
 修改登录密码判断方法
 http请求很容易被截获，在写登录模块时，直接使用明文密码请求，很容易明文密码泄露；
      若在js页面对密码进行一次加密后在传输，虽不是明文密码，但也完全可以截获加密后的暗文，
      伪造http请求进行登录。为了防止密码泄露，通过参考各种方案，找到了以下比较好实现的方法：
 1、登录请求分两次进行，第一次仅传用户名
 2、服务器收到用户名后，生成一串随机数，将随机数响应给客户端，并将用户名和随机数存到session
 3、客户端收到响应后，将密码和随机数安一定的规则组合起来，再md5加密，再http请求
      （此时保证了每一次登录请求的密码会随随机数的不同而不同，这个随机数为服务器生成，
      相当于一个公钥，与本次登录操作唯一且一一对应，客户端无法伪造）
 4、服务器收到请求，取出session中的用户名和随机数串，核对用户名，再取数据库中的正确密码，
      再按相同的规则与随机数组合并md5加密，再比较请求的密码暗文，返回登录结果。
 拼接该次登录请求所得存在session的uuid所得的字符串再经md5加密得到的二次暗文密码
 即二次暗文密码=MD5(MD5(明文密码)+uuid)
 数据库中存放的经MD5加密的暗文密码拼接session中存放的uuid再经MD5加密得到的校验字符串
```
- isLock // 判断当前ip或用户名是否被锁定

- tryLock // 为了防止用户暴力破解,当客服登录时输入了错误的账号或密码时,系统会把当前ip或用户名记录到Redis中

- getUserUuid // 服务器收到用户名后，生成一串随机数，将随机数响应给客户端，并将用户名和随机数存到session

- checkLoginValid // (实现异地登录互斥)前端定时发送请求到此接口校验目前登录状态是否有效,无效则强制前端退出登录
***

### com.chatopera.cc.app.handler.apps.internet.IMController
### (玩家端聊天功能相关)
- getOnlineCustomerServiceList // 获取在线客服接口

- text // 根据接入网站标识码,进行相关数据(如聊天类型,是否自选特定客服)处理,然后跳转index接口

- index // 聊天页面渲染接口(根据text接口处理先关信息后跳转过来,进行玩家名称处理->注入支付相关信息->页面模板选择)

- putPayInfo // 向聊天页面注入支付相关信息

- getPayAccountByWeightsRandomly // 随机获取相关账号,加上权重功能

- replaceNickName // 更新玩家在聊天窗口显示的历史用户名(由于彬哥说不显示历史聊天记录,此方法暂弃用)

- payInfoView // 客服咨询充值信息页面跳转
***

### com.chatopera.cc.app.handler.apps.service.ChatServiceController
### (客服端聊天功能相关)
- index // 客服聊天历史

- current // 客服当前聊天列表

- end // 结束对话

- clean // 清理客服(已结束的)聊天列表
***

### com.chatopera.cc.app.handler.admin.users.UsersController 
### (客服管理相关)
- index // 客服列表

- add // 创建客服页面跳转接口

- save // 创建客服保存接口

- update // 更新客服保存接口

- delete // 删除客服接口
***

### 坐席分配方法流
- 1.com.chatopera.cc.app.im.handler.IMEventHandler#onConnect
- 2.com.chatopera.cc.util.OnlineUserUtils#newRequestMessage
- 3.com.chatopera.cc.app.im.router.MessageRouter#handler
- 4.com.chatopera.cc.app.algorithm.AutomaticServiceDist#allotAgent
***


## 附录1-数据字典
<div class="uk-width-medium-3-4">
    <h1 id="春松客服-数据字典生成日期-2019-07-10"
        style="text-align:center;">春松客服-数据字典<span style="font-size:14px;color: #ccc;margin-left:20px;">(生成日期: 2019-07-10)</span></h1>
    <p>版本：3.9.0</p>
    <p></p>
    <h2 id="外呼计划表">外呼计划表 </h2>
    <table>
        <caption>cs_callout_dialplan 外呼计划表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 计划名称</td>
        </tr>
        <tr class="odd">
            <td>voicechannel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 语音渠道ID</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 技能组部门ID</td>
        </tr>
        <tr class="odd">
            <td>isrecord</td>
            <td>bit(1)</td>
            <td> b'1'</td>
            <td> NO</td>
            <td></td>
            <td> 是否录音</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 创建日期</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 更新日期</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>isarchive</td>
            <td>bit(1)</td>
            <td> b'0'</td>
            <td> NO</td>
            <td></td>
            <td> 是否已删除</td>
        </tr>
        <tr class="even">
            <td>executed</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 执行次数</td>
        </tr>
        <tr class="odd">
            <td>targetnum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 目标客户总数</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(20)</td>
            <td> stopped</td>
            <td> NO</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>executefirsttime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 首次执行时间</td>
        </tr>
        <tr class="even">
            <td>executelasttime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最近一次执行时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>maxconcurrence</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 最大并发数</td>
        </tr>
        <tr class="odd">
            <td>curconcurrence</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 当前并发数</td>
        </tr>
        <tr class="even">
            <td>concurrenceratio</td>
            <td>float(10,5)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 坐席并发比</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="拨号计划记录">拨号计划记录 </h2>
    <table>
        <caption>cs_callout_log_dialplan 拨号计划记录</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>operator</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作员ID</td>
        </tr>
        <tr class="odd">
            <td>module</td>
            <td>varchar(128)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模块</td>
        </tr>
        <tr class="even">
            <td>subitem</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 子项</td>
        </tr>
        <tr class="odd">
            <td>operation</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作内容</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作状态</td>
        </tr>
        <tr class="odd">
            <td>detail</td>
            <td>varchar(128)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 细节</td>
        </tr>
        <tr class="even">
            <td>ipaddr</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户ip地址</td>
        </tr>
        <tr class="odd">
            <td>port</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户ip端口</td>
        </tr>
        <tr class="even">
            <td>url</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> url</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="外呼计划目标客户">外呼计划目标客户 </h2>
    <table>
        <caption>cs_callout_targets 外呼计划目标客户</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>organid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 部门ID</td>
        </tr>
        <tr class="even">
            <td>calls</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 拨打次数</td>
        </tr>
        <tr class="odd">
            <td>failed</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 拨打失败次数</td>
        </tr>
        <tr class="even">
            <td>invalid</td>
            <td>bit(1)</td>
            <td> b'0'</td>
            <td> NO</td>
            <td></td>
            <td> 是否有效</td>
        </tr>
        <tr class="odd">
            <td>phone</td>
            <td>varchar(20)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 电话号码</td>
        </tr>
        <tr class="even">
            <td>country</td>
            <td>varchar(20)</td>
            <td> 中国</td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="odd">
            <td>province</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>dialplan</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 关联呼叫计划</td>
        </tr>
        <tr class="even">
            <td>contactsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关联联系人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="机器人客服表">机器人客服表 </h2>
    <table>
        <caption>cs_chatbot 机器人客服表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>base_url</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 基础URL</td>
        </tr>
        <tr class="odd">
            <td>client_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> Client Id</td>
        </tr>
        <tr class="even">
            <td>secret</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> Client Secret</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 部门ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 聊天机器人名字</td>
        </tr>
        <tr class="odd">
            <td>description</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="even">
            <td>primary_language</td>
            <td>varchar(20)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 首选语言</td>
        </tr>
        <tr class="odd">
            <td>fallback</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 兜底回复</td>
        </tr>
        <tr class="even">
            <td>welcome</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 欢迎语</td>
        </tr>
        <tr class="odd">
            <td>channel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 渠道类型</td>
        </tr>
        <tr class="even">
            <td>sns_account_identifier</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 渠道标识</td>
        </tr>
        <tr class="odd">
            <td>enabled</td>
            <td>tinyint(1)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否开启</td>
        </tr>
        <tr class="even">
            <td>workmode</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 工作模式</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="联系人笔记表">联系人笔记表 </h2>
    <table>
        <caption>cs_contact_notes 联系人笔记表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> ID</td>
        </tr>
        <tr class="even">
            <td>contactid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>category</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容类型</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>datastatus</td>
            <td>tinyint(1)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 是否已删除</td>
        </tr>
        <tr class="odd">
            <td>agentuser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 在线访客记录ID</td>
        </tr>
        <tr class="even">
            <td>onlineuser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 在线访客信息ID</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租客标识</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="文件">文件 </h2>
    <table>
        <caption>cs_stream_file 文件</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 文件ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(300)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 文件名称</td>
        </tr>
        <tr class="odd">
            <td>data</td>
            <td>mediumblob</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 原始文件</td>
        </tr>
        <tr class="even">
            <td>thumbnail</td>
            <td>mediumblob</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 缩略图</td>
        </tr>
        <tr class="odd">
            <td>mime</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件类型 Content Type</td>
        </tr>
        <tr class="even">
            <td>cooperation</td>
            <td>mediumblob</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 协作文件</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销活动">电销活动 </h2>
    <table>
        <caption>uk_act_batch 电销活动</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>ACTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>NAMENUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的名单总数</td>
        </tr>
        <tr class="even">
            <td>VALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的有效名单总数</td>
        </tr>
        <tr class="odd">
            <td>INVALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的无效名单总数</td>
        </tr>
        <tr class="even">
            <td>ASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 已分配名单总数</td>
        </tr>
        <tr class="odd">
            <td>NOTASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 未分配名单总数</td>
        </tr>
        <tr class="even">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="odd">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>AREA</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        <tr class="odd">
            <td>imptype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务导入类型</td>
        </tr>
        <tr class="even">
            <td>batchtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次类型</td>
        </tr>
        <tr class="odd">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="even">
            <td>impurl</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 导入的文件链接</td>
        </tr>
        <tr class="odd">
            <td>filetype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件类型</td>
        </tr>
        <tr class="even">
            <td>dbtype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库类型</td>
        </tr>
        <tr class="odd">
            <td>jdbcurl</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库连接地址</td>
        </tr>
        <tr class="even">
            <td>driverclazz</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库驱动</td>
        </tr>
        <tr class="odd">
            <td>password</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="even">
            <td>DESCRIPTION</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据描述</td>
        </tr>
        <tr class="odd">
            <td>execnum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导入次数</td>
        </tr>
        <tr class="even">
            <td>SOURCE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="odd">
            <td>CLAZZ</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 驱动名称</td>
        </tr>
        <tr class="even">
            <td>TASKFIRETIME</td>
            <td>timestamp</td>
            <td> CURRENT_TIMESTAMP</td>
            <td> NO</td>
            <td></td>
            <td> 任务启动时间</td>
        </tr>
        <tr class="odd">
            <td>CRAWLTASKID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务ID</td>
        </tr>
        <tr class="even">
            <td>EMAIL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮箱地址</td>
        </tr>
        <tr class="odd">
            <td>NICKNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 昵称</td>
        </tr>
        <tr class="even">
            <td>USERID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>TASKTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务类型</td>
        </tr>
        <tr class="even">
            <td>TASKID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务ID</td>
        </tr>
        <tr class="odd">
            <td>FETCHER</td>
            <td>smallint(6)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 采集状态</td>
        </tr>
        <tr class="even">
            <td>PAUSE</td>
            <td>smallint(6)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 是否暂停</td>
        </tr>
        <tr class="odd">
            <td>PLANTASK</td>
            <td>smallint(6)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 是否计划任务</td>
        </tr>
        <tr class="even">
            <td>SECURE_ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 安全级别</td>
        </tr>
        <tr class="odd">
            <td>CONFIGURE_ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 配置ID</td>
        </tr>
        <tr class="even">
            <td>TAKSPLAN_ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 计划任务ID</td>
        </tr>
        <tr class="odd">
            <td>CRAWLTASK</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 采集任务状态</td>
        </tr>
        <tr class="even">
            <td>TARGETTASK</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标任务</td>
        </tr>
        <tr class="odd">
            <td>STARTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序位置</td>
        </tr>
        <tr class="even">
            <td>LASTDATE</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最近一次更新时间</td>
        </tr>
        <tr class="odd">
            <td>CREATETABLE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 自动创建数据表</td>
        </tr>
        <tr class="even">
            <td>MEMO</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>NEXTFIRETIME</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 下次启动时间</td>
        </tr>
        <tr class="even">
            <td>CRONEXP</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 表达式</td>
        </tr>
        <tr class="odd">
            <td>TASKSTATUS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务状态</td>
        </tr>
        <tr class="even">
            <td>usearea</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户区域</td>
        </tr>
        <tr class="odd">
            <td>areafield</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域字段</td>
        </tr>
        <tr class="even">
            <td>areafieldtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域字段类型</td>
        </tr>
        <tr class="odd">
            <td>arearule</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域规则</td>
        </tr>
        <tr class="even">
            <td>minareavalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 范围最小值</td>
        </tr>
        <tr class="odd">
            <td>maxareavalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 范围最大值</td>
        </tr>
        <tr class="even">
            <td>formatstr</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 格式化字符串</td>
        </tr>
        <tr class="odd">
            <td>DATAID</td>
            <td>varchar(1000)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表id字符串</td>
        </tr>
        <tr class="even">
            <td>DICID</td>
            <td>varchar(1000)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录id字符串</td>
        </tr>
        <tr class="odd">
            <td>taskinfo</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> taskinfo信息</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销活动名单分配表">电销活动名单分配表 </h2>
    <table>
        <caption>uk_act_callagent 电销活动名单分配表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>FILTERTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选类型（批次筛选/元数据筛选）</td>
        </tr>
        <tr class="even">
            <td>BATID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单使用的批次ID</td>
        </tr>
        <tr class="odd">
            <td>TABLEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单使用元数据ID</td>
        </tr>
        <tr class="even">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="odd">
            <td>INX</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述信息</td>
        </tr>
        <tr class="even">
            <td>distype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配类型</td>
        </tr>
        <tr class="odd">
            <td>distarget</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配对象</td>
        </tr>
        <tr class="even">
            <td>disnum</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配数据</td>
        </tr>
        <tr class="odd">
            <td>ACTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销名单表">电销名单表 </h2>
    <table>
        <caption>uk_act_callnames 电销名单表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名单名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名单代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名单状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>ACTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="even">
            <td>BATID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="odd">
            <td>DATASTATUS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>CALLS</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 拨打次数</td>
        </tr>
        <tr class="odd">
            <td>FAILDCALLS</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 拨打失败次数</td>
        </tr>
        <tr class="even">
            <td>invalid</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="odd">
            <td>failed</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>WORKSTATUS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名单业务状态</td>
        </tr>
        <tr class="odd">
            <td>OPTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配时间</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>BATNAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次名称</td>
        </tr>
        <tr class="even">
            <td>TASKNAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务名称</td>
        </tr>
        <tr class="odd">
            <td>owneruser</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属用户</td>
        </tr>
        <tr class="even">
            <td>ownerdept</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属部门</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>taskid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务ID</td>
        </tr>
        <tr class="odd">
            <td>filterid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单ID</td>
        </tr>
        <tr class="even">
            <td>phonenumber</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电话号码</td>
        </tr>
        <tr class="odd">
            <td>leavenum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 剩余名单数量</td>
        </tr>
        <tr class="even">
            <td>metaname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 元数据名称</td>
        </tr>
        <tr class="odd">
            <td>distype</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配类型</td>
        </tr>
        <tr class="even">
            <td>previewtime</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 预览时长</td>
        </tr>
        <tr class="odd">
            <td>previewtimes</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 预览次数</td>
        </tr>
        <tr class="even">
            <td>servicetype</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务类型</td>
        </tr>
        <tr class="odd">
            <td>reservation</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否预约</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 预约备注</td>
        </tr>
        <tr class="odd">
            <td>firstcalltime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 首次拨打时间</td>
        </tr>
        <tr class="even">
            <td>firstcallstatus</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 首次拨打状态</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销配置表">电销配置表 </h2>
    <table>
        <caption>uk_act_config 电销配置表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人用户名</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>enablecallout</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用自动外呼功能</td>
        </tr>
        <tr class="even">
            <td>countdown</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 倒计时时长</td>
        </tr>
        <tr class="odd">
            <td>enabletagentthreads</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用坐席外呼并发控制</td>
        </tr>
        <tr class="even">
            <td>agentthreads</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席外呼并发数量</td>
        </tr>
        <tr class="odd">
            <td>enabletaithreads</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用机器人外呼并发控制</td>
        </tr>
        <tr class="even">
            <td>aithreads</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 机器人并发数量</td>
        </tr>
        <tr class="odd">
            <td>defaultvalue</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        <tr class="even">
            <td>strategy</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 策略</td>
        </tr>
        <tr class="odd">
            <td>type</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>previewautocallout</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用预览倒计时</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="筛选记录表">筛选记录表 </h2>
    <table>
        <caption>uk_act_filter_his 筛选记录表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>ACTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>NAMENUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的名单总数</td>
        </tr>
        <tr class="even">
            <td>VALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的有效名单总数</td>
        </tr>
        <tr class="odd">
            <td>INVALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的无效名单总数</td>
        </tr>
        <tr class="even">
            <td>ASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 已分配名单总数</td>
        </tr>
        <tr class="odd">
            <td>NOTASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 未分配名单总数</td>
        </tr>
        <tr class="even">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="odd">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>execnum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导入次数</td>
        </tr>
        <tr class="odd">
            <td>SOURCE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="even">
            <td>BATID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>FILTERID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单ID</td>
        </tr>
        <tr class="even">
            <td>ASSIGNEDORGAN</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分配部门</td>
        </tr>
        <tr class="odd">
            <td>exectype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行类型</td>
        </tr>
        <tr class="even">
            <td>renum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分配数量</td>
        </tr>
        <tr class="odd">
            <td>reorgannum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 部门分配数量</td>
        </tr>
        <tr class="even">
            <td>assignedai</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分配到AI的名单数量</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="筛选表单">筛选表单 </h2>
    <table>
        <caption>uk_act_formfilter 筛选表单</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人名称</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>FILTERTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选类型（批次筛选/元数据筛选）</td>
        </tr>
        <tr class="even">
            <td>BATID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单使用的批次ID</td>
        </tr>
        <tr class="odd">
            <td>TABLEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单使用元数据ID</td>
        </tr>
        <tr class="even">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="odd">
            <td>INX</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注信息</td>
        </tr>
        <tr class="even">
            <td>execnum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导入次数</td>
        </tr>
        <tr class="odd">
            <td>filternum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 筛选次数</td>
        </tr>
        <tr class="even">
            <td>conditional</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 条件个数</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="筛选项">筛选项 </h2>
    <table>
        <caption>uk_act_formfilter_item 筛选项</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>formfilterid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选器ID</td>
        </tr>
        <tr class="odd">
            <td>field</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段</td>
        </tr>
        <tr class="even">
            <td>cond</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 条件</td>
        </tr>
        <tr class="odd">
            <td>value</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 取值</td>
        </tr>
        <tr class="even">
            <td>contype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 条件类型</td>
        </tr>
        <tr class="odd">
            <td>itemtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>comp</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 逻辑条件</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销角色授权表">电销角色授权表 </h2>
    <table>
        <caption>uk_act_role 电销角色授权表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>rolename</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色名称</td>
        </tr>
        <tr class="odd">
            <td>roleid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色id</td>
        </tr>
        <tr class="even">
            <td>bustype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 业务类型</td>
        </tr>
        <tr class="odd">
            <td>organid</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 授权部门id</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销日程表">电销日程表 </h2>
    <table>
        <caption>uk_act_schedule 电销日程表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人名称</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>FILTERTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选类型（批次筛选/元数据筛选）</td>
        </tr>
        <tr class="even">
            <td>ACTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单使用的批次ID</td>
        </tr>
        <tr class="odd">
            <td>TABLEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单使用元数据ID</td>
        </tr>
        <tr class="even">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="odd">
            <td>INX</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述信息</td>
        </tr>
        <tr class="even">
            <td>daytype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期类型</td>
        </tr>
        <tr class="odd">
            <td>begintime</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始时间</td>
        </tr>
        <tr class="even">
            <td>endtime</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束时间</td>
        </tr>
        <tr class="odd">
            <td>policy</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 策略</td>
        </tr>
        <tr class="even">
            <td>callvalues</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注内容信息</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销任务表">电销任务表 </h2>
    <table>
        <caption>uk_act_task 电销任务表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人名称</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>ACTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>NAMENUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的名单总数</td>
        </tr>
        <tr class="even">
            <td>VALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的有效名单总数</td>
        </tr>
        <tr class="odd">
            <td>INVALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的无效名单总数</td>
        </tr>
        <tr class="even">
            <td>ASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 已分配名单总数</td>
        </tr>
        <tr class="odd">
            <td>NOTASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 未分配名单总数</td>
        </tr>
        <tr class="even">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="odd">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注信息</td>
        </tr>
        <tr class="even">
            <td>execnum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导入次数</td>
        </tr>
        <tr class="odd">
            <td>SOURCE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源信息</td>
        </tr>
        <tr class="even">
            <td>BATID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>FILTERID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选ID</td>
        </tr>
        <tr class="even">
            <td>ASSIGNEDORGAN</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分配给部门</td>
        </tr>
        <tr class="odd">
            <td>exectype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行类型</td>
        </tr>
        <tr class="even">
            <td>renum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分配数量</td>
        </tr>
        <tr class="odd">
            <td>reorgannum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分配到部门数量</td>
        </tr>
        <tr class="even">
            <td>assignedai</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分配到AI的名单数量</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="客服客户端广告位表">客服客户端广告位表 </h2>
    <table>
        <caption>uk_ad_position 客服客户端广告位表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人名称</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库分类上级ID</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="even">
            <td>AREA</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        <tr class="odd">
            <td>IMGURL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图片URL</td>
        </tr>
        <tr class="even">
            <td>TIPTEXT</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 提示文本</td>
        </tr>
        <tr class="odd">
            <td>URL</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 路径</td>
        </tr>
        <tr class="even">
            <td>CONTENT</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="odd">
            <td>WEIGHT</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 权重</td>
        </tr>
        <tr class="even">
            <td>ADTYPE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 广告类型</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 广告状态</td>
        </tr>
        <tr class="even">
            <td>ADPOS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 广告位置</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="在线客服服务记录表">在线客服服务记录表 </h2>
    <table>
        <caption>uk_agentservice 在线客服服务记录表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人用户名</td>
        </tr>
        <tr class="odd">
            <td>agentno</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="even">
            <td>userid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>channel</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="even">
            <td>logindate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录时间</td>
        </tr>
        <tr class="odd">
            <td>source</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="even">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束时间</td>
        </tr>
        <tr class="odd">
            <td>nickname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 昵称</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>province</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="even">
            <td>country</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="odd">
            <td>headImgUrl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 头像URL</td>
        </tr>
        <tr class="even">
            <td>waittingtime</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 等待时间</td>
        </tr>
        <tr class="odd">
            <td>tokenum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 未回复消息数量</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>appid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="even">
            <td>sessiontype</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话类型</td>
        </tr>
        <tr class="odd">
            <td>contextid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>agentserviceid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录ID</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>snsuser</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID（微信）</td>
        </tr>
        <tr class="odd">
            <td>lastmessage</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后一条消息时间</td>
        </tr>
        <tr class="even">
            <td>waittingtimestart</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 进入排队时间</td>
        </tr>
        <tr class="odd">
            <td>lastgetmessage</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席最后一条消息时间</td>
        </tr>
        <tr class="even">
            <td>lastmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后一条消息内容</td>
        </tr>
        <tr class="odd">
            <td>agentskill</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="even">
            <td>create_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>update_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>update_user</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人</td>
        </tr>
        <tr class="even">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配目标用户</td>
        </tr>
        <tr class="odd">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="even">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="odd">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属人</td>
        </tr>
        <tr class="even">
            <td>datadept</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人部门</td>
        </tr>
        <tr class="odd">
            <td>intime</td>
            <td>int(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入时间</td>
        </tr>
        <tr class="even">
            <td>batid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>ipaddr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP地址</td>
        </tr>
        <tr class="even">
            <td>osname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作系统名称</td>
        </tr>
        <tr class="odd">
            <td>browser</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 浏览器</td>
        </tr>
        <tr class="even">
            <td>sessiontimes</td>
            <td>int(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话时长</td>
        </tr>
        <tr class="odd">
            <td>servicetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务时长</td>
        </tr>
        <tr class="even">
            <td>region</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域</td>
        </tr>
        <tr class="odd">
            <td>agentusername</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席用户名</td>
        </tr>
        <tr class="even">
            <td>times</td>
            <td>int(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息数量</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>contactsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="odd">
            <td>createdate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息到达时间</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客填写的姓名</td>
        </tr>
        <tr class="odd">
            <td>email</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客填写的邮件地址</td>
        </tr>
        <tr class="even">
            <td>phone</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客填写的电话号码</td>
        </tr>
        <tr class="odd">
            <td>resion</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客填写的来访原因</td>
        </tr>
        <tr class="even">
            <td>satisfaction</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 满意度调查评级</td>
        </tr>
        <tr class="odd">
            <td>satistime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 满意度调查时间</td>
        </tr>
        <tr class="even">
            <td>satislevel</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 满意度评分</td>
        </tr>
        <tr class="odd">
            <td>satiscomment</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 满意度备注</td>
        </tr>
        <tr class="even">
            <td>trans</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否转接</td>
        </tr>
        <tr class="odd">
            <td>transtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 转接时间</td>
        </tr>
        <tr class="even">
            <td>transmemo</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 转接备注</td>
        </tr>
        <tr class="odd">
            <td>agentreplyinterval</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席评级回复间隔</td>
        </tr>
        <tr class="even">
            <td>agentreplytime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席评级回复时间</td>
        </tr>
        <tr class="odd">
            <td>avgreplyinterval</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客平均回复间隔</td>
        </tr>
        <tr class="even">
            <td>avgreplytime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客回复总时长</td>
        </tr>
        <tr class="odd">
            <td>agentreplys</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复消息数量</td>
        </tr>
        <tr class="even">
            <td>userasks</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客发送消息数量</td>
        </tr>
        <tr class="odd">
            <td>agentuserid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客ID</td>
        </tr>
        <tr class="even">
            <td>sessionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>qualitystatus</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检状态</td>
        </tr>
        <tr class="even">
            <td>qualitydisorgan</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检分配部门</td>
        </tr>
        <tr class="odd">
            <td>qualitydisuser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检分配用户</td>
        </tr>
        <tr class="even">
            <td>qualityorgan</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检部门</td>
        </tr>
        <tr class="odd">
            <td>qualityuser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检用户</td>
        </tr>
        <tr class="even">
            <td>qualitytime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检时间</td>
        </tr>
        <tr class="odd">
            <td>qualitytype</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检方式</td>
        </tr>
        <tr class="even">
            <td>qualityscore</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 质检打分</td>
        </tr>
        <tr class="odd">
            <td>solvestatus</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问题解决状态</td>
        </tr>
        <tr class="even">
            <td>leavemsg</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否留言</td>
        </tr>
        <tr class="odd">
            <td>initiator</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话发起方</td>
        </tr>
        <tr class="even">
            <td>agenttimeout</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席超时时长</td>
        </tr>
        <tr class="odd">
            <td>agenttimeouttimes</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席超时次数</td>
        </tr>
        <tr class="even">
            <td>servicetimeout</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>agentservicetimeout</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 服务超时</td>
        </tr>
        <tr class="even">
            <td>agentfrewords</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席触发敏感词</td>
        </tr>
        <tr class="odd">
            <td>servicefrewords</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 访客触发敏感词</td>
        </tr>
        <tr class="even">
            <td>leavemsgstatus</td>
            <td>varchar(20)</td>
            <td> notprocess</td>
            <td> YES</td>
            <td></td>
            <td> 留言处理状态</td>
        </tr>
        <tr class="odd">
            <td>agent</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席</td>
        </tr>
        <tr class="even">
            <td>skill</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="odd">
            <td>endby</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 终止方</td>
        </tr>
        <tr class="even">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AIID</td>
        </tr>
        <tr class="odd">
            <td>aiservice</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否AI服务</td>
        </tr>
        <tr class="even">
            <td>foragent</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 直接转人工</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="在线客服坐席状态表">在线客服坐席状态表 </h2>
    <table>
        <caption>uk_agentstatus 在线客服坐席状态表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>agentno</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="odd">
            <td>logindate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录时间</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>agentserviceid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务ID</td>
        </tr>
        <tr class="odd">
            <td>serusernum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 服务用户数</td>
        </tr>
        <tr class="even">
            <td>skill</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="odd">
            <td>skillname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组名称</td>
        </tr>
        <tr class="even">
            <td>users</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 接入用户数</td>
        </tr>
        <tr class="odd">
            <td>maxusers</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 最大接入用户数</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>update_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>update_user</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人</td>
        </tr>
        <tr class="even">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配目标用户</td>
        </tr>
        <tr class="odd">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="even">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="odd">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属人</td>
        </tr>
        <tr class="even">
            <td>datadept</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人部门</td>
        </tr>
        <tr class="odd">
            <td>batid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="even">
            <td>pulluser</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否允许拉取用户</td>
        </tr>
        <tr class="odd">
            <td>busy</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 示忙</td>
        </tr>
        <tr class="even">
            <td>workstatus</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 工作状态</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="在线客服访客咨询表">在线客服访客咨询表 </h2>
    <table>
        <caption>uk_agentuser 在线客服访客咨询表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>agentno</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="even">
            <td>userid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>channel</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="even">
            <td>logindate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录时间</td>
        </tr>
        <tr class="odd">
            <td>source</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="even">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束时间</td>
        </tr>
        <tr class="odd">
            <td>nickname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 昵称</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>province</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="even">
            <td>country</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="odd">
            <td>headImgUrl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 头像URL</td>
        </tr>
        <tr class="even">
            <td>waittingtime</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 等待时长</td>
        </tr>
        <tr class="odd">
            <td>tokenum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 接入次数</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>appid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="even">
            <td>sessiontype</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话类型</td>
        </tr>
        <tr class="odd">
            <td>contextid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>agentserviceid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录ID</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>snsuser</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNS用户ID</td>
        </tr>
        <tr class="odd">
            <td>lastmessage</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后一条消息时间</td>
        </tr>
        <tr class="even">
            <td>waittingtimestart</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 进入队列时间</td>
        </tr>
        <tr class="odd">
            <td>lastgetmessage</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后一条消息时间</td>
        </tr>
        <tr class="even">
            <td>lastmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后一条消息</td>
        </tr>
        <tr class="odd">
            <td>agentskill</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="even">
            <td>create_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>update_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>update_user</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人</td>
        </tr>
        <tr class="even">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配目标用户</td>
        </tr>
        <tr class="odd">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="even">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="odd">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属人</td>
        </tr>
        <tr class="even">
            <td>datadept</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人部门</td>
        </tr>
        <tr class="odd">
            <td>intime</td>
            <td>int(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入时间</td>
        </tr>
        <tr class="even">
            <td>batid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>opttype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务处理类型</td>
        </tr>
        <tr class="even">
            <td>ipaddr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP地址</td>
        </tr>
        <tr class="odd">
            <td>osname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作系统名称</td>
        </tr>
        <tr class="even">
            <td>browser</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 浏览器</td>
        </tr>
        <tr class="odd">
            <td>sessiontimes</td>
            <td>int(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话时长</td>
        </tr>
        <tr class="even">
            <td>servicetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务次数</td>
        </tr>
        <tr class="odd">
            <td>region</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地区</td>
        </tr>
        <tr class="even">
            <td>agentservice</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务ID</td>
        </tr>
        <tr class="odd">
            <td>warnings</td>
            <td>varchar(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 提醒次数</td>
        </tr>
        <tr class="even">
            <td>warningtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 提醒时间</td>
        </tr>
        <tr class="odd">
            <td>reptime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席最后一次回复时间</td>
        </tr>
        <tr class="even">
            <td>reptimes</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复次数</td>
        </tr>
        <tr class="odd">
            <td>skill</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="even">
            <td>agent</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户录入的姓名</td>
        </tr>
        <tr class="even">
            <td>phone</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客录入的电话</td>
        </tr>
        <tr class="odd">
            <td>email</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客录入的邮件</td>
        </tr>
        <tr class="even">
            <td>resion</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客录入的来访原因</td>
        </tr>
        <tr class="odd">
            <td>agentreplyinterval</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复总间隔</td>
        </tr>
        <tr class="even">
            <td>agentreplytime</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复时长</td>
        </tr>
        <tr class="odd">
            <td>agentreplys</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复次数</td>
        </tr>
        <tr class="even">
            <td>userasks</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 访客提问次数</td>
        </tr>
        <tr class="odd">
            <td>avgreplyinterval</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 平均回复间隔</td>
        </tr>
        <tr class="even">
            <td>avgreplytime</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 平均回复时长</td>
        </tr>
        <tr class="odd">
            <td>sessionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>url</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> URL</td>
        </tr>
        <tr class="even">
            <td>traceid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 跟踪ID</td>
        </tr>
        <tr class="odd">
            <td>agenttimeout</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席超时时长</td>
        </tr>
        <tr class="even">
            <td>agenttimeouttimes</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席超时次数</td>
        </tr>
        <tr class="odd">
            <td>servicetimeout</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 服务超时时长</td>
        </tr>
        <tr class="even">
            <td>agentservicetimeout</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 服务超时次数</td>
        </tr>
        <tr class="odd">
            <td>agentfrewords</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席触发敏感词</td>
        </tr>
        <tr class="even">
            <td>servicefrewords</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 访客触发敏感词</td>
        </tr>
        <tr class="odd">
            <td>topflag</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否置顶</td>
        </tr>
        <tr class="even">
            <td>toptimes</td>
            <td>int(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 置顶时长</td>
        </tr>
        <tr class="odd">
            <td>toptime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 置顶时间</td>
        </tr>
        <tr class="even">
            <td>firstreplytime</td>
            <td>int(20)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 首次响应时间</td>
        </tr>
        <tr class="odd">
            <td>agentusername</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席姓名</td>
        </tr>
        <tr class="even">
            <td>alarm</td>
            <td>int(10)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否触发预警</td>
        </tr>
        <tr class="odd">
            <td>initiator</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话发起方</td>
        </tr>
        <tr class="even">
            <td>chatbotops</td>
            <td>tinyint(1)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否是机器人客服</td>
        </tr>
        <tr class="odd">
            <td>chatbotlogicerror</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 机器人客服不符合逻辑返回累计</td>
        </tr>
        <tr class="even">
            <td>chatbotround</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 机器人客服对话轮次</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="访客联系人关联表">访客联系人关联表 </h2>
    <table>
        <caption>uk_agentuser_contacts 访客联系人关联表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>appid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="even">
            <td>channel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>contactsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人用户名</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="ai机器人表">AI机器人表 </h2>
    <table>
        <caption>uk_ai AI机器人表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AI名称</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户id</td>
        </tr>
        <tr class="even">
            <td>inx</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>description</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        <tr class="odd">
            <td>code</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="ai列表">AI列表 </h2>
    <table>
        <caption>uk_ai_snsaccount AI列表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>snsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="区域类型">区域类型 </h2>
    <table>
        <caption>uk_area_type 区域类型</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库分类上级ID</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="even">
            <td>AREA</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="附件表">附件表 </h2>
    <table>
        <caption>uk_attachment_file 附件表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组织机构ID</td>
        </tr>
        <tr class="even">
            <td>datastatus</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态（逻辑删除）</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>url</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地址</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>filelength</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件长度</td>
        </tr>
        <tr class="odd">
            <td>filetype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件类型</td>
        </tr>
        <tr class="even">
            <td>image</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否图片</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>model</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属功能模块</td>
        </tr>
        <tr class="odd">
            <td>fileid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件ID</td>
        </tr>
        <tr class="even">
            <td>modelid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属模块数据ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="黑名单表">黑名单表 </h2>
    <table>
        <caption>uk_blacklist 黑名单表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>contactid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="odd">
            <td>sessionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>channel</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创家人</td>
        </tr>
        <tr class="odd">
            <td>agentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="even">
            <td>phone</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电话号码</td>
        </tr>
        <tr class="odd">
            <td>openid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信号</td>
        </tr>
        <tr class="even">
            <td>description</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="odd">
            <td>agentserviceid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席服务ID</td>
        </tr>
        <tr class="even">
            <td>times</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 次数</td>
        </tr>
        <tr class="odd">
            <td>chattime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话次数</td>
        </tr>
        <tr class="even">
            <td>controltime</td>
            <td>int(11)</td>
            <td> 1</td>
            <td> YES</td>
            <td></td>
            <td> 开始时间</td>
        </tr>
        <tr class="odd">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束时间</td>
        </tr>
        <tr class="even">
            <td>agentuser</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="业务流程表">业务流程表 </h2>
    <table>
        <caption>uk_bpm_process 业务流程表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>CONTENT</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程文本内容</td>
        </tr>
        <tr class="even">
            <td>STATUS</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="odd">
            <td>TITLE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程标题</td>
        </tr>
        <tr class="even">
            <td>PUBLISHED</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程发布状态</td>
        </tr>
        <tr class="odd">
            <td>PROCESSID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="even">
            <td>PROCESSTYPE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程类型</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="坐席监控表">坐席监控表 </h2>
    <table>
        <caption>uk_call_monitor 坐席监控表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> ID</td>
        </tr>
        <tr class="even">
            <td>USERID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录人ID</td>
        </tr>
        <tr class="odd">
            <td>AGENT</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工号</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席用户名（登录名）</td>
        </tr>
        <tr class="odd">
            <td>AGENTNO</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机号（坐席登录的分机号码）</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席姓名</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席状态code（对应字典表里的CODE）</td>
        </tr>
        <tr class="even">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席当前状态（坐席当前状态（坐席监控首页显示，判断根本依据，每次状态改变，数据记录会被更新））</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>AGENTSERVICEID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>SKILL</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组ID</td>
        </tr>
        <tr class="even">
            <td>SKILLNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组名称</td>
        </tr>
        <tr class="odd">
            <td>BUSY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否忙</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 记录创建时间（每个坐席的第一条记录为，点击登录之后，登录成功之后的时间，则会插入一条记录。以后每次状态改变，记录会被更新，时间都会跟着改变，变为状态改变后的时间。）</td>
        </tr>
        <tr class="odd">
            <td>ANI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫号码</td>
        </tr>
        <tr class="even">
            <td>CALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫号码</td>
        </tr>
        <tr class="odd">
            <td>DIRECTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向</td>
        </tr>
        <tr class="even">
            <td>CALLSTARTTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话开始时间</td>
        </tr>
        <tr class="odd">
            <td>CALLENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话结束时间</td>
        </tr>
        <tr class="even">
            <td>RINGDURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 振铃时长</td>
        </tr>
        <tr class="odd">
            <td>DURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话时长</td>
        </tr>
        <tr class="even">
            <td>MISSCALL</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否漏话</td>
        </tr>
        <tr class="odd">
            <td>RECORD</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否录音</td>
        </tr>
        <tr class="even">
            <td>RECORDTIME</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音时长</td>
        </tr>
        <tr class="odd">
            <td>STARTRECORD</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始录音时间</td>
        </tr>
        <tr class="even">
            <td>ENDRECORD</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束录音时间</td>
        </tr>
        <tr class="odd">
            <td>RECORDFILENAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件名（单纯录音文件名）</td>
        </tr>
        <tr class="even">
            <td>RECORDFILE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件全路径名（存放位置+文件名）</td>
        </tr>
        <tr class="odd">
            <td>SOURCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="even">
            <td>ANSWERTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 应答时间</td>
        </tr>
        <tr class="odd">
            <td>CURRENT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前通话</td>
        </tr>
        <tr class="even">
            <td>INIT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 初始通话</td>
        </tr>
        <tr class="odd">
            <td>ACTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件动作</td>
        </tr>
        <tr class="even">
            <td>HOST</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间主机（FreeWitch主机帐户名）</td>
        </tr>
        <tr class="odd">
            <td>IPADDR</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机IP（FreeWitch主机IP）</td>
        </tr>
        <tr class="even">
            <td>SERVICESUMMARY</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否记录服务小结</td>
        </tr>
        <tr class="odd">
            <td>SERVICEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录ID</td>
        </tr>
        <tr class="even">
            <td>SERVICESTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前呼叫状态</td>
        </tr>
        <tr class="odd">
            <td>CHANNELSTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件中的呼叫状态</td>
        </tr>
        <tr class="even">
            <td>COUNTRY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电国家</td>
        </tr>
        <tr class="odd">
            <td>PROVINCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码归属省份</td>
        </tr>
        <tr class="even">
            <td>CITY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码归属城市</td>
        </tr>
        <tr class="odd">
            <td>ISP</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码运营商</td>
        </tr>
        <tr class="even">
            <td>CONTACTSID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="odd">
            <td>EXTENTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机ID</td>
        </tr>
        <tr class="even">
            <td>HOSTID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="odd">
            <td>CALLTYPE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向类型 | 计费类型</td>
        </tr>
        <tr class="even">
            <td>CALLDIR</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 我方呼叫方向</td>
        </tr>
        <tr class="odd">
            <td>OTHERDIR</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对方呼叫方向</td>
        </tr>
        <tr class="even">
            <td>BRIDGEID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 桥接ID</td>
        </tr>
        <tr class="odd">
            <td>BRIDRE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否有桥接</td>
        </tr>
        <tr class="even">
            <td>DISCALLER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫分机号</td>
        </tr>
        <tr class="odd">
            <td>DISCALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫分机号</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属组织机构ID</td>
        </tr>
        <tr class="odd">
            <td>EVENTID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="坐席绩效表">坐席绩效表 </h2>
    <table>
        <caption>uk_call_performance 坐席绩效表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="even">
            <td>USERID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录人ID</td>
        </tr>
        <tr class="odd">
            <td>AGENT</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工号</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席用户名（登录名）</td>
        </tr>
        <tr class="odd">
            <td>AGENTNO</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机号（坐席登录的分机号码）</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席姓名</td>
        </tr>
        <tr class="odd">
            <td>STARTSTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上一个状态</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席状态code（对应字典管理中的CODE）</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席历史状态（插入该表时的状态（复制自坐席监控表的状态））</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>AGENTSERVICEID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>SKILL</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组ID</td>
        </tr>
        <tr class="odd">
            <td>SKILLNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组名称</td>
        </tr>
        <tr class="even">
            <td>BUSY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否忙</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态开始时间（取值（坐席监控表的记录创建时间））</td>
        </tr>
        <tr class="even">
            <td>ENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 记录创建时间（取值（纪录插入表时的时间））</td>
        </tr>
        <tr class="odd">
            <td>INTERVALTIME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态持续时间（秒）（endtime - createtime = intervaltime）</td>
        </tr>
        <tr class="even">
            <td>ANI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫号码</td>
        </tr>
        <tr class="odd">
            <td>CALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫号码</td>
        </tr>
        <tr class="even">
            <td>DIRECTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向</td>
        </tr>
        <tr class="odd">
            <td>CALLSTARTTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话开始时间</td>
        </tr>
        <tr class="even">
            <td>CALLENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话结束时间</td>
        </tr>
        <tr class="odd">
            <td>RINGDURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 振铃时长</td>
        </tr>
        <tr class="even">
            <td>DURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话时长</td>
        </tr>
        <tr class="odd">
            <td>MISSCALL</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否漏话</td>
        </tr>
        <tr class="even">
            <td>RECORD</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否录音</td>
        </tr>
        <tr class="odd">
            <td>RECORDTIME</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音时长</td>
        </tr>
        <tr class="even">
            <td>STARTRECORD</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始录音时间</td>
        </tr>
        <tr class="odd">
            <td>ENDRECORD</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束录音时间</td>
        </tr>
        <tr class="even">
            <td>RECORDFILENAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件名（单纯录音文件名）</td>
        </tr>
        <tr class="odd">
            <td>RECORDFILE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件全路径名（存放位置+文件名）</td>
        </tr>
        <tr class="even">
            <td>SOURCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="odd">
            <td>ANSWERTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 应答时间</td>
        </tr>
        <tr class="even">
            <td>CURRENT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前通话</td>
        </tr>
        <tr class="odd">
            <td>INIT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 初始通话</td>
        </tr>
        <tr class="even">
            <td>ACTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件动作</td>
        </tr>
        <tr class="odd">
            <td>HOST</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间主机（FreeWitch主机帐户名）</td>
        </tr>
        <tr class="even">
            <td>IPADDR</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机IP（FreeWitch主机IP）</td>
        </tr>
        <tr class="odd">
            <td>SERVICESUMMARY</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否记录服务小结</td>
        </tr>
        <tr class="even">
            <td>SERVICEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录ID</td>
        </tr>
        <tr class="odd">
            <td>SERVICESTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前呼叫状态</td>
        </tr>
        <tr class="even">
            <td>CHANNELSTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件中的呼叫状态</td>
        </tr>
        <tr class="odd">
            <td>COUNTRY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电国家</td>
        </tr>
        <tr class="even">
            <td>PROVINCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码归属省份</td>
        </tr>
        <tr class="odd">
            <td>CITY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码归属城市</td>
        </tr>
        <tr class="even">
            <td>ISP</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码运营商</td>
        </tr>
        <tr class="odd">
            <td>CONTACTSID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="even">
            <td>EXTENTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机ID</td>
        </tr>
        <tr class="odd">
            <td>HOSTID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="even">
            <td>CALLTYPE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向类型 | 计费类型</td>
        </tr>
        <tr class="odd">
            <td>CALLDIR</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 我方呼叫方向</td>
        </tr>
        <tr class="even">
            <td>OTHERDIR</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对方呼叫方向</td>
        </tr>
        <tr class="odd">
            <td>BRIDGEID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 桥接ID</td>
        </tr>
        <tr class="even">
            <td>BRIDRE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否有桥接</td>
        </tr>
        <tr class="odd">
            <td>DISCALLER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫分机号</td>
        </tr>
        <tr class="even">
            <td>DISCALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫分机号</td>
        </tr>
        <tr class="odd">
            <td>SATISF</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否进行满意度调查</td>
        </tr>
        <tr class="even">
            <td>SATISFACTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务小结</td>
        </tr>
        <tr class="odd">
            <td>SATISFDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 满意度调查提交时间</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属组织机构ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="呼叫中心acl表">呼叫中心ACL表 </h2>
    <table>
        <caption>uk_callcenter_acl 呼叫中心ACL表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>strategy</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 策略内容</td>
        </tr>
        <tr class="even">
            <td>defaultvalue</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="通话记录表">通话记录表 </h2>
    <table>
        <caption>uk_callcenter_event 通话记录表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(100)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>organid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>SOURCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="even">
            <td>ANSWER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 应答时间</td>
        </tr>
        <tr class="odd">
            <td>scurrent</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否当前通话</td>
        </tr>
        <tr class="even">
            <td>INIT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 初始通话</td>
        </tr>
        <tr class="odd">
            <td>CALLER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫发起号码</td>
        </tr>
        <tr class="even">
            <td>CALLING</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫对象</td>
        </tr>
        <tr class="odd">
            <td>CALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫号码</td>
        </tr>
        <tr class="even">
            <td>AGENTYPE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席类型</td>
        </tr>
        <tr class="odd">
            <td>QUENE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 队列名称</td>
        </tr>
        <tr class="even">
            <td>ANI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫号码</td>
        </tr>
        <tr class="odd">
            <td>TOUSER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标用户</td>
        </tr>
        <tr class="even">
            <td>DIRECTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向</td>
        </tr>
        <tr class="odd">
            <td>STATE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>AGENT</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工号</td>
        </tr>
        <tr class="odd">
            <td>agentname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席名字</td>
        </tr>
        <tr class="even">
            <td>ACTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件动作</td>
        </tr>
        <tr class="odd">
            <td>HOST</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间主机</td>
        </tr>
        <tr class="even">
            <td>IPADDR</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机IP</td>
        </tr>
        <tr class="odd">
            <td>LOCALDATETIME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间发起时间</td>
        </tr>
        <tr class="even">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态代码</td>
        </tr>
        <tr class="odd">
            <td>TIME</td>
            <td>decimal(20,0)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间秒值</td>
        </tr>
        <tr class="even">
            <td>STARTTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话开始时间</td>
        </tr>
        <tr class="odd">
            <td>ENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话结束时间</td>
        </tr>
        <tr class="even">
            <td>DURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话时长</td>
        </tr>
        <tr class="odd">
            <td>INSIDE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内线</td>
        </tr>
        <tr class="even">
            <td>MISSCALL</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否漏话</td>
        </tr>
        <tr class="odd">
            <td>srecord</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否录音</td>
        </tr>
        <tr class="even">
            <td>RECORDTIME</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音时长</td>
        </tr>
        <tr class="odd">
            <td>STARTRECORD</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始录音时间</td>
        </tr>
        <tr class="even">
            <td>ENDRECORD</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束录音时间</td>
        </tr>
        <tr class="odd">
            <td>ANSWERTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 应答时间</td>
        </tr>
        <tr class="even">
            <td>RINGDURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 振铃时长</td>
        </tr>
        <tr class="odd">
            <td>SERVICESUMMARY</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否记录服务小结</td>
        </tr>
        <tr class="even">
            <td>SERVICEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录ID</td>
        </tr>
        <tr class="odd">
            <td>RECORDFILE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件名</td>
        </tr>
        <tr class="even">
            <td>CALLBACK</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回呼</td>
        </tr>
        <tr class="odd">
            <td>CCQUENE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 转接队列</td>
        </tr>
        <tr class="even">
            <td>SERVICESTATUS</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前呼叫状态</td>
        </tr>
        <tr class="odd">
            <td>CHANNELSTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件中的呼叫状态</td>
        </tr>
        <tr class="even">
            <td>COUNTRY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电国家</td>
        </tr>
        <tr class="odd">
            <td>PROVINCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码归属省份</td>
        </tr>
        <tr class="even">
            <td>CITY</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电归属号码城市</td>
        </tr>
        <tr class="odd">
            <td>ISP</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来电号码运营商</td>
        </tr>
        <tr class="even">
            <td>VOICECALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语音呼叫</td>
        </tr>
        <tr class="odd">
            <td>CONTACTSID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="even">
            <td>EXTENTION</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机ID</td>
        </tr>
        <tr class="odd">
            <td>HOSTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="even">
            <td>CALLTYPE</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向类型|计费类型</td>
        </tr>
        <tr class="odd">
            <td>CALLDIR</td>
            <td>varchar(30)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向</td>
        </tr>
        <tr class="even">
            <td>OTHERDIR</td>
            <td>varchar(30)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对边呼叫方向</td>
        </tr>
        <tr class="odd">
            <td>OTHERLEGDEST</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫另一方号码</td>
        </tr>
        <tr class="even">
            <td>BRIDGEID</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 桥接ID</td>
        </tr>
        <tr class="odd">
            <td>BRIDGE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否有桥接</td>
        </tr>
        <tr class="even">
            <td>RECORDFILENAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件名</td>
        </tr>
        <tr class="odd">
            <td>DISCALLER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示主叫</td>
        </tr>
        <tr class="even">
            <td>DISCALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示被叫</td>
        </tr>
        <tr class="odd">
            <td>SATISF</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 满意度</td>
        </tr>
        <tr class="even">
            <td>SATISFACTION</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 满意度结果</td>
        </tr>
        <tr class="odd">
            <td>SATISFDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 满意度时间</td>
        </tr>
        <tr class="even">
            <td>datestr</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席通话日期（yyyy-MM-dd）用于每小时通话数量折线图</td>
        </tr>
        <tr class="odd">
            <td>hourstr</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席通话时间小时（HH）用于每小时通话数量折线图</td>
        </tr>
        <tr class="even">
            <td>taskid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务ID</td>
        </tr>
        <tr class="odd">
            <td>actid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="even">
            <td>batid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>statustype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 号码隐藏状态</td>
        </tr>
        <tr class="odd">
            <td>disphonenum</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 号码</td>
        </tr>
        <tr class="even">
            <td>distype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示类型</td>
        </tr>
        <tr class="odd">
            <td>nameid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名单ID</td>
        </tr>
        <tr class="even">
            <td>siptrunk</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 拨打的网关</td>
        </tr>
        <tr class="odd">
            <td>prefix</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否在号码前加拨0</td>
        </tr>
        <tr class="even">
            <td>dialplan</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫计划ID</td>
        </tr>
        <tr class="odd">
            <td>callid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> FreeSwitch通话ID</td>
        </tr>
        <tr class="even">
            <td>voicechannel</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语音渠道标识</td>
        </tr>
        <tr class="odd">
            <td>recordingfile</td>
            <td>varchar(150)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件标识</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="分机信息表">分机信息表 </h2>
    <table>
        <caption>uk_callcenter_extention 分机信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>extention</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机号</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务ID</td>
        </tr>
        <tr class="even">
            <td>agentno</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工号</td>
        </tr>
        <tr class="odd">
            <td>password</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="even">
            <td>callout</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许呼出</td>
        </tr>
        <tr class="odd">
            <td>playnum</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 播报工号</td>
        </tr>
        <tr class="even">
            <td>srecord</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>extype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机类型</td>
        </tr>
        <tr class="even">
            <td>description</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="odd">
            <td>subtype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机类型</td>
        </tr>
        <tr class="even">
            <td>mediapath</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 播报工号语音文件</td>
        </tr>
        <tr class="odd">
            <td>afterprocess</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席通话后启用后处理功能</td>
        </tr>
        <tr class="even">
            <td>siptrunk</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>enableai</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用AI机器人</td>
        </tr>
        <tr class="even">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AI机器人</td>
        </tr>
        <tr class="odd">
            <td>sceneid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用场景配置</td>
        </tr>
        <tr class="even">
            <td>welcomemsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人欢迎语</td>
        </tr>
        <tr class="odd">
            <td>waitmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人等待提示语</td>
        </tr>
        <tr class="even">
            <td>tipmessage</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人提示客户说话</td>
        </tr>
        <tr class="odd">
            <td>asrrecordpath</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> ASR结果路径</td>
        </tr>
        <tr class="even">
            <td>ttsrecordpath</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> ASR结果路径</td>
        </tr>
        <tr class="odd">
            <td>errormessage</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 异常提示消息</td>
        </tr>
        <tr class="even">
            <td>enablewebrtc</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用WebRTC</td>
        </tr>
        <tr class="odd">
            <td>bustype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 业务类型（电销sale/问卷quesurvey）</td>
        </tr>
        <tr class="even">
            <td>proid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> （产品ID）</td>
        </tr>
        <tr class="odd">
            <td>queid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> （问卷ID）</td>
        </tr>
        <tr class="even">
            <td>aitype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人类型（smartai/quesurvey）</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="ivr菜单表">IVR菜单表 </h2>
    <table>
        <caption>uk_callcenter_ivr IVR菜单表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>greetlong</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 欢迎提示语音</td>
        </tr>
        <tr class="even">
            <td>greetshort</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 欢迎提示短语音</td>
        </tr>
        <tr class="odd">
            <td>invalidsound</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 无效输入提示语音</td>
        </tr>
        <tr class="even">
            <td>exitsound</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 离开语音</td>
        </tr>
        <tr class="odd">
            <td>confirmmacro</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 确认宏指令</td>
        </tr>
        <tr class="even">
            <td>confirmkey</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 确认按键</td>
        </tr>
        <tr class="odd">
            <td>ttsengine</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> TTS引擎</td>
        </tr>
        <tr class="even">
            <td>ttsvoice</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> TTS语音</td>
        </tr>
        <tr class="odd">
            <td>confirmattempts</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 确认提示消息</td>
        </tr>
        <tr class="even">
            <td>timeout</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 超时时间</td>
        </tr>
        <tr class="odd">
            <td>interdigittimeout</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫等待超时</td>
        </tr>
        <tr class="even">
            <td>maxfailures</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最大失败次数</td>
        </tr>
        <tr class="odd">
            <td>maxtimeouts</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最大外呼次数</td>
        </tr>
        <tr class="even">
            <td>digitlen</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数字长度</td>
        </tr>
        <tr class="odd">
            <td>menucontent</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IVR菜单内容</td>
        </tr>
        <tr class="even">
            <td>action</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指令</td>
        </tr>
        <tr class="odd">
            <td>digits</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 拨号键</td>
        </tr>
        <tr class="even">
            <td>param</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数</td>
        </tr>
        <tr class="odd">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="even">
            <td>extentionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机ID</td>
        </tr>
        <tr class="odd">
            <td>enableai</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用AI机器人</td>
        </tr>
        <tr class="even">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AI机器人</td>
        </tr>
        <tr class="odd">
            <td>sceneid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用场景配置</td>
        </tr>
        <tr class="even">
            <td>welcomemsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人欢迎语</td>
        </tr>
        <tr class="odd">
            <td>waitmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人等待提示语</td>
        </tr>
        <tr class="even">
            <td>tipmessage</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人提示客户说话</td>
        </tr>
        <tr class="odd">
            <td>asrrecordpath</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> ASR结果路径</td>
        </tr>
        <tr class="even">
            <td>ttsrecordpath</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> ASR结果路径</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="媒体资源表">媒体资源表 </h2>
    <table>
        <caption>uk_callcenter_media 媒体资源表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>filename</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件名</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件类型</td>
        </tr>
        <tr class="odd">
            <td>filelength</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语音文件长度</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="fs服务器信息">FS服务器信息 </h2>
    <table>
        <caption>log.error("知识库内容表 快捷回复表  使用了es QuickReplyRepositoryImpl "); FS服务器信息</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机名</td>
        </tr>
        <tr class="even">
            <td>port</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 端口</td>
        </tr>
        <tr class="odd">
            <td>password</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="even">
            <td>ipaddr</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP地址</td>
        </tr>
        <tr class="odd">
            <td>callbacknumber</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回呼号码</td>
        </tr>
        <tr class="even">
            <td>autoanswer</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用自动接听</td>
        </tr>
        <tr class="odd">
            <td>callcenter</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用呼叫中心功能</td>
        </tr>
        <tr class="even">
            <td>recordpath</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 录音文件位置</td>
        </tr>
        <tr class="odd">
            <td>ivrpath</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IVR文件位置</td>
        </tr>
        <tr class="even">
            <td>fspath</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> FS安装路径</td>
        </tr>
        <tr class="odd">
            <td>device</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语音设备类型</td>
        </tr>
        <tr class="even">
            <td>callbacktype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回呼送号号码</td>
        </tr>
        <tr class="odd">
            <td>sipautoanswer</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SIP自动应答</td>
        </tr>
        <tr class="even">
            <td>abscodec</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通信编码</td>
        </tr>
        <tr class="odd">
            <td>enableai</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用机器人</td>
        </tr>
        <tr class="even">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人ID</td>
        </tr>
        <tr class="odd">
            <td>sceneid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 场景ID</td>
        </tr>
        <tr class="even">
            <td>welcomemsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人欢迎语</td>
        </tr>
        <tr class="odd">
            <td>waitmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人等待提示语</td>
        </tr>
        <tr class="even">
            <td>tipmessage</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人提示客户说话</td>
        </tr>
        <tr class="odd">
            <td>asrrecordpath</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> ASR结果路径</td>
        </tr>
        <tr class="even">
            <td>ttsrecordpath</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> ASR结果路径</td>
        </tr>
        <tr class="odd">
            <td>afterprocess</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席通话后启用后处理功能</td>
        </tr>
        <tr class="even">
            <td>enablewebrtc</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用WebRTC</td>
        </tr>
        <tr class="odd">
            <td>webrtcaddress</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> WebRTC地址</td>
        </tr>
        <tr class="even">
            <td>webrtcport</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> WebRTC端口</td>
        </tr>
        <tr class="odd">
            <td>webrtcssl</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> WebRTC启用SSL</td>
        </tr>
        <tr class="even">
            <td>dissipphone</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 外呼隐藏话机上的号码</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="拨号计划表">拨号计划表 </h2>
    <table>
        <caption>uk_callcenter_router 拨号计划表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>regex</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 匹配正则</td>
        </tr>
        <tr class="even">
            <td>allow</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许</td>
        </tr>
        <tr class="odd">
            <td>falsebreak</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 异常终止</td>
        </tr>
        <tr class="even">
            <td>routerinx</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编号</td>
        </tr>
        <tr class="odd">
            <td>routercontent</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 路由规则</td>
        </tr>
        <tr class="even">
            <td>field</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段名称</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="sip网关信息表">SIP网关信息表 </h2>
    <table>
        <caption>uk_callcenter_siptrunk SIP网关信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 组件ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SIP中继名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>sipcontent</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SIP配置内容</td>
        </tr>
        <tr class="even">
            <td>sipserver</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务器地址</td>
        </tr>
        <tr class="odd">
            <td>extention</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 转分机号</td>
        </tr>
        <tr class="even">
            <td>outnumber</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 出局号码</td>
        </tr>
        <tr class="odd">
            <td>prefix</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 前缀</td>
        </tr>
        <tr class="even">
            <td>port</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 端口</td>
        </tr>
        <tr class="odd">
            <td>exptime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 超时时长</td>
        </tr>
        <tr class="even">
            <td>retry</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重试次数</td>
        </tr>
        <tr class="odd">
            <td>register</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否注册</td>
        </tr>
        <tr class="even">
            <td>fromuser</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否添加FROM</td>
        </tr>
        <tr class="odd">
            <td>transprotocol</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 协议</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>authuser</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 认证用户名</td>
        </tr>
        <tr class="even">
            <td>password</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="odd">
            <td>protocol</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 协议</td>
        </tr>
        <tr class="even">
            <td>heartbeat</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 心跳时长</td>
        </tr>
        <tr class="odd">
            <td>dtmf</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> DTMF协议</td>
        </tr>
        <tr class="even">
            <td>province</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="odd">
            <td>city</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="even">
            <td>defaultsip</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 默认网关</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>busyext</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席忙的时候转到号码</td>
        </tr>
        <tr class="odd">
            <td>notready</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席不在线的时候转到号码</td>
        </tr>
        <tr class="even">
            <td>noname</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 未找到名单或未分配的时候转到号码</td>
        </tr>
        <tr class="odd">
            <td>enablecallagent</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席不在线转手机</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电话技能组表">电话技能组表 </h2>
    <table>
        <caption>uk_callcenter_skill 电话技能组表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>skill</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组名称</td>
        </tr>
        <tr class="even">
            <td>password</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="odd">
            <td>quene</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 队列名称</td>
        </tr>
        <tr class="even">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="技能组对应表">技能组对应表 </h2>
    <table>
        <caption>uk_callcenter_skillext 技能组对应表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td> 主键ID</td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>skillid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组ID</td>
        </tr>
        <tr class="even">
            <td>extention</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX服务器ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="坐席对话表">坐席对话表 </h2>
    <table>
        <caption>uk_chat_message 坐席对话表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>type</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="odd">
            <td>calltype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话方向</td>
        </tr>
        <tr class="even">
            <td>contextid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>usession</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>touser</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标用户</td>
        </tr>
        <tr class="odd">
            <td>channel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="even">
            <td>tousername</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标用户名</td>
        </tr>
        <tr class="odd">
            <td>appid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="even">
            <td>userid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>nickname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 昵称</td>
        </tr>
        <tr class="even">
            <td>message</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息内容</td>
        </tr>
        <tr class="odd">
            <td>msgtype</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息类型</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>msgid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息ID（微信）</td>
        </tr>
        <tr class="even">
            <td>expmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 原始消息</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>decimal(20,0)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>update_user</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配目标用户</td>
        </tr>
        <tr class="even">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="odd">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="even">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属人</td>
        </tr>
        <tr class="odd">
            <td>datadept</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人部门</td>
        </tr>
        <tr class="even">
            <td>batid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>model</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息所属组件</td>
        </tr>
        <tr class="even">
            <td>chatype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话类型</td>
        </tr>
        <tr class="odd">
            <td>agentserviceid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席服务ID</td>
        </tr>
        <tr class="even">
            <td>mediaid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 媒体文件ID（微信）</td>
        </tr>
        <tr class="odd">
            <td>locx</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地理位置</td>
        </tr>
        <tr class="even">
            <td>locy</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地理位置</td>
        </tr>
        <tr class="odd">
            <td>duration</td>
            <td>varchar(30)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话时长</td>
        </tr>
        <tr class="even">
            <td>scale</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地图级别</td>
        </tr>
        <tr class="odd">
            <td>filename</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件名</td>
        </tr>
        <tr class="even">
            <td>filesize</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件尺寸</td>
        </tr>
        <tr class="odd">
            <td>attachmentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 附件ID</td>
        </tr>
        <tr class="even">
            <td>lastagentmsgtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最近一次坐席发送消息时间</td>
        </tr>
        <tr class="odd">
            <td>agentreplytime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复消息时间</td>
        </tr>
        <tr class="even">
            <td>lastmsgtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客最近一次发送消息时间</td>
        </tr>
        <tr class="odd">
            <td>agentreplyinterval</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复消息时间</td>
        </tr>
        <tr class="even">
            <td>sessionid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>cooperation</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 合并消息</td>
        </tr>
        <tr class="even">
            <td>datastatus</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态（已删除/未删除）</td>
        </tr>
        <tr class="odd">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 机器人ID</td>
        </tr>
        <tr class="even">
            <td>topic</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否命中知识库</td>
        </tr>
        <tr class="odd">
            <td>topicid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 命中知识库ID</td>
        </tr>
        <tr class="even">
            <td>topicatid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 命中知识库分类ID</td>
        </tr>
        <tr class="odd">
            <td>aichat</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否在和AI对话</td>
        </tr>
        <tr class="even">
            <td>suggestmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 推荐的提示信息</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="报表字段映射表">报表字段映射表 </h2>
    <table>
        <caption>uk_columnproperties 报表字段映射表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>format</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 格式化</td>
        </tr>
        <tr class="odd">
            <td>prefix</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 前缀</td>
        </tr>
        <tr class="even">
            <td>width</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件宽度</td>
        </tr>
        <tr class="odd">
            <td>suffix</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件后缀</td>
        </tr>
        <tr class="even">
            <td>font</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字体</td>
        </tr>
        <tr class="odd">
            <td>colname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段名称</td>
        </tr>
        <tr class="even">
            <td>border</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件宽度</td>
        </tr>
        <tr class="odd">
            <td>decimalCount</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数字格式化</td>
        </tr>
        <tr class="even">
            <td>sepsymbol</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件标签</td>
        </tr>
        <tr class="odd">
            <td>alignment</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对齐方式</td>
        </tr>
        <tr class="even">
            <td>fontStyle</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字体样式</td>
        </tr>
        <tr class="odd">
            <td>fontColor</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字体颜色</td>
        </tr>
        <tr class="even">
            <td>paramName</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数名称</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>modelid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件ID</td>
        </tr>
        <tr class="even">
            <td>dataname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>cur</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 位置</td>
        </tr>
        <tr class="even">
            <td>hyp</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 超链</td>
        </tr>
        <tr class="odd">
            <td>timeFormat</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间格式化</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序位置</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="访客网站配置表">访客网站配置表 </h2>
    <table>
        <caption>uk_consult_invite 访客网站配置表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>impid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>owner</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据拥有人</td>
        </tr>
        <tr class="odd">
            <td>processid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="even">
            <td>shares</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="odd">
            <td>update_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>update_user</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>wfstatus</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_model</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请模式</td>
        </tr>
        <tr class="even">
            <td>consult_invite_content</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框文本</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_position</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框位置</td>
        </tr>
        <tr class="even">
            <td>consult_invite_color</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框颜色</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_right</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框距右边位置</td>
        </tr>
        <tr class="even">
            <td>consult_invite_left</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框距左侧</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_bottom</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框距下边位置</td>
        </tr>
        <tr class="even">
            <td>consult_invite_top</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框距顶部位置</td>
        </tr>
        <tr class="odd">
            <td>create_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_width</td>
            <td>int(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框宽度</td>
        </tr>
        <tr class="even">
            <td>consult_invite_poptype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框悬浮位置</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_fontsize</td>
            <td>int(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框文本字体</td>
        </tr>
        <tr class="even">
            <td>consult_invite_fontstyle</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框文本样式</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_fontcolor</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框文本颜色</td>
        </tr>
        <tr class="even">
            <td>consult_invite_interval</td>
            <td>int(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框弹出频率</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_repeat</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框背景平铺</td>
        </tr>
        <tr class="even">
            <td>consult_invite_hight</td>
            <td>int(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框高度</td>
        </tr>
        <tr class="odd">
            <td>snsaccountid</td>
            <td>varchar(56)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="even">
            <td>consult_vsitorbtn_position</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮图标位置</td>
        </tr>
        <tr class="odd">
            <td>consult_vsitorbtn_content</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框文本</td>
        </tr>
        <tr class="even">
            <td>consult_vsitorbtn_right</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框距右侧位置</td>
        </tr>
        <tr class="odd">
            <td>consult_vsitorbtn_left</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框距左侧位置</td>
        </tr>
        <tr class="even">
            <td>consult_vsitorbtn_top</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框距顶部</td>
        </tr>
        <tr class="odd">
            <td>consult_vsitorbtn_color</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框颜色</td>
        </tr>
        <tr class="even">
            <td>consult_vsitorbtn_model</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框模式</td>
        </tr>
        <tr class="odd">
            <td>consult_vsitorbtn_bottom</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框距底部</td>
        </tr>
        <tr class="even">
            <td>consult_invite_backimg</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 悬浮框背景图片</td>
        </tr>
        <tr class="odd">
            <td>datadept</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据部门</td>
        </tr>
        <tr class="even">
            <td>agent_online</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席在线</td>
        </tr>
        <tr class="odd">
            <td>consult_dialog_color</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框颜色</td>
        </tr>
        <tr class="even">
            <td>consult_dialog_logo</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框LOGO</td>
        </tr>
        <tr class="odd">
            <td>consult_dialog_headimg</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框头像</td>
        </tr>
        <tr class="even">
            <td>consult_vsitorbtn_display</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示按钮</td>
        </tr>
        <tr class="odd">
            <td>dialog_name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话显示名称</td>
        </tr>
        <tr class="even">
            <td>dialog_address</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框地址</td>
        </tr>
        <tr class="odd">
            <td>dialog_phone</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框电话号码</td>
        </tr>
        <tr class="even">
            <td>dialog_mail</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框邮件</td>
        </tr>
        <tr class="odd">
            <td>dialog_introduction</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框介绍</td>
        </tr>
        <tr class="even">
            <td>dialog_message</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框欢迎信息</td>
        </tr>
        <tr class="odd">
            <td>dialog_ad</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对话框广告</td>
        </tr>
        <tr class="even">
            <td>consult_invite_enable</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用邀请框</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_accept</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框统一按钮文本</td>
        </tr>
        <tr class="even">
            <td>consult_invite_later</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 延迟弹出邀请框</td>
        </tr>
        <tr class="odd">
            <td>consult_invite_delay</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框延迟时间</td>
        </tr>
        <tr class="even">
            <td>consult_invite_bg</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请框背景图片</td>
        </tr>
        <tr class="odd">
            <td>leavemessage</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言提示文本</td>
        </tr>
        <tr class="even">
            <td>lvmname</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言框显示名称字段</td>
        </tr>
        <tr class="odd">
            <td>lvmphone</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言卡电话号码字段</td>
        </tr>
        <tr class="even">
            <td>lvmemail</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言卡Email字段</td>
        </tr>
        <tr class="odd">
            <td>lvmaddress</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言卡地址字段</td>
        </tr>
        <tr class="even">
            <td>lvmqq</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 留言框QQ</td>
        </tr>
        <tr class="odd">
            <td>lvmcontent</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言卡留言内容字段</td>
        </tr>
        <tr class="even">
            <td>workinghours</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 工作时间段</td>
        </tr>
        <tr class="odd">
            <td>lvmopentype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言卡弹出模式</td>
        </tr>
        <tr class="even">
            <td>skill</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="odd">
            <td>notinwhmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 非工作时间段提示文本</td>
        </tr>
        <tr class="even">
            <td>consult_skill_logo</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组图标</td>
        </tr>
        <tr class="odd">
            <td>consult_skill_title</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组提示标题</td>
        </tr>
        <tr class="even">
            <td>consult_skill_img</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组显示背景图片</td>
        </tr>
        <tr class="odd">
            <td>consult_skill_msg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组提示文本内容</td>
        </tr>
        <tr class="even">
            <td>consult_skill_numbers</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示技能组成员数量</td>
        </tr>
        <tr class="odd">
            <td>consult_skill_maxagent</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示技能组下最大用户数</td>
        </tr>
        <tr class="even">
            <td>consult_skill_bottomtitle</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组底部标题</td>
        </tr>
        <tr class="odd">
            <td>consult_skill_agent</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否显示技能组下的坐席</td>
        </tr>
        <tr class="even">
            <td>ai</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用AI</td>
        </tr>
        <tr class="odd">
            <td>aifirst</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> AI优先显示</td>
        </tr>
        <tr class="even">
            <td>aisearch</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> AI搜索文本</td>
        </tr>
        <tr class="odd">
            <td>aimsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AI欢迎信息</td>
        </tr>
        <tr class="even">
            <td>aisuccesstip</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AI成功命中提示消息</td>
        </tr>
        <tr class="odd">
            <td>ainame</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人名称</td>
        </tr>
        <tr class="even">
            <td>consult_info</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用咨询信息收集功能</td>
        </tr>
        <tr class="odd">
            <td>consult_info_name</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 填写姓名</td>
        </tr>
        <tr class="even">
            <td>consult_info_email</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 填写 邮件地址</td>
        </tr>
        <tr class="odd">
            <td>consult_info_phone</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 填写 电话号码</td>
        </tr>
        <tr class="even">
            <td>consult_info_resion</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 填写咨询问题</td>
        </tr>
        <tr class="odd">
            <td>consult_info_message</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 咨询窗口显示的欢迎语</td>
        </tr>
        <tr class="even">
            <td>consult_info_cookies</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 在Cookies中存储用户信息</td>
        </tr>
        <tr class="odd">
            <td>recordhis</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否记录访问轨迹</td>
        </tr>
        <tr class="even">
            <td>traceuser</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否跟踪访客</td>
        </tr>
        <tr class="odd">
            <td>onlyareaskill</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 区域技能组</td>
        </tr>
        <tr class="even">
            <td>uk_consult_invite</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 提示消息</td>
        </tr>
        <tr class="odd">
            <td>areaskilltipmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域技能组提示消息</td>
        </tr>
        <tr class="even">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认的AI</td>
        </tr>
        <tr class="odd">
            <td>maxwordsnum</td>
            <td>int(11)</td>
            <td> 300</td>
            <td> YES</td>
            <td></td>
            <td> 访客端允许输入的最大字数</td>
        </tr>
        <tr class="even">
            <td>agentshortcutkey</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席默认回复消息快捷键</td>
        </tr>
        <tr class="odd">
            <td>usershortcutkey</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客默认回复消息快捷键</td>
        </tr>
        <tr class="even">
            <td>agentctrlenter</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用坐席端CTRL+Enter发送消息</td>
        </tr>
        <tr class="odd">
            <td>ctrlenter</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用访客端CTRL+Enter发送消息</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="联系人信息表">联系人信息表 </h2>
    <table>
        <caption>uk_contacts 联系人信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="even">
            <td>gender</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人性别</td>
        </tr>
        <tr class="odd">
            <td>cusbirthday</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 出生日期</td>
        </tr>
        <tr class="even">
            <td>ctype</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人类型</td>
        </tr>
        <tr class="odd">
            <td>ckind</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人类别</td>
        </tr>
        <tr class="even">
            <td>clevel</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人级别</td>
        </tr>
        <tr class="odd">
            <td>ccode</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人代码</td>
        </tr>
        <tr class="even">
            <td>nickname</td>
            <td>varchar(64)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人昵称</td>
        </tr>
        <tr class="odd">
            <td>sarea</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发货地址区县</td>
        </tr>
        <tr class="even">
            <td>csource</td>
            <td>varchar(64)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人来源</td>
        </tr>
        <tr class="odd">
            <td>language</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语言</td>
        </tr>
        <tr class="even">
            <td>marriage</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 婚姻状况</td>
        </tr>
        <tr class="odd">
            <td>education</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 学历</td>
        </tr>
        <tr class="even">
            <td>identifytype</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 证件类型</td>
        </tr>
        <tr class="odd">
            <td>identifynumber</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 证件号码</td>
        </tr>
        <tr class="even">
            <td>website</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 网址</td>
        </tr>
        <tr class="odd">
            <td>email</td>
            <td>varchar(128)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电子邮件</td>
        </tr>
        <tr class="even">
            <td>emailalt</td>
            <td>varchar(128)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用电子邮件</td>
        </tr>
        <tr class="odd">
            <td>mobileno</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 手机号码</td>
        </tr>
        <tr class="even">
            <td>mobilealt</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用手机号码</td>
        </tr>
        <tr class="odd">
            <td>phone</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 办公电话</td>
        </tr>
        <tr class="even">
            <td>extension</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 办公分机</td>
        </tr>
        <tr class="odd">
            <td>phonealt</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用办公电话</td>
        </tr>
        <tr class="even">
            <td>extensionalt</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用办公分机</td>
        </tr>
        <tr class="odd">
            <td>familyphone</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 住宅电话</td>
        </tr>
        <tr class="even">
            <td>familyphonealt</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用住宅电话</td>
        </tr>
        <tr class="odd">
            <td>fax</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 传真号码</td>
        </tr>
        <tr class="even">
            <td>faxalt</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用传真号码</td>
        </tr>
        <tr class="odd">
            <td>country</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="even">
            <td>province</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省</td>
        </tr>
        <tr class="odd">
            <td>city</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 市(区)县</td>
        </tr>
        <tr class="even">
            <td>address</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地址</td>
        </tr>
        <tr class="odd">
            <td>postcode</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮政编码</td>
        </tr>
        <tr class="even">
            <td>enterpriseid</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 企(事)业单位</td>
        </tr>
        <tr class="odd">
            <td>company</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 公司</td>
        </tr>
        <tr class="even">
            <td>department</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>duty</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 职务</td>
        </tr>
        <tr class="even">
            <td>deptpr</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 直接上级领导</td>
        </tr>
        <tr class="odd">
            <td>validstatus</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 效力状态</td>
        </tr>
        <tr class="even">
            <td>weixin</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信号</td>
        </tr>
        <tr class="odd">
            <td>weixinname</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信昵称</td>
        </tr>
        <tr class="even">
            <td>weixinid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信ID</td>
        </tr>
        <tr class="odd">
            <td>weibo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微博昵称</td>
        </tr>
        <tr class="even">
            <td>weiboid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微博ID</td>
        </tr>
        <tr class="odd">
            <td>qqcode</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> QQ账号</td>
        </tr>
        <tr class="even">
            <td>touchtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后联系时间</td>
        </tr>
        <tr class="odd">
            <td>datastatus</td>
            <td>tinyint(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>processid</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人姓名</td>
        </tr>
        <tr class="odd">
            <td>updateuser</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人ID</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人备注</td>
        </tr>
        <tr class="odd">
            <td>updateusername</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人姓名</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户标识</td>
        </tr>
        <tr class="even">
            <td>compper</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配目标用户</td>
        </tr>
        <tr class="even">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="odd">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="even">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属人</td>
        </tr>
        <tr class="odd">
            <td>datadept</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人部门</td>
        </tr>
        <tr class="even">
            <td>entcusid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户ID</td>
        </tr>
        <tr class="odd">
            <td>pinyin</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 拼音</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="立方体表">立方体表 </h2>
    <table>
        <caption>uk_cube 立方体表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>timestamp</td>
            <td> 0000-00-00 00:00:00</td>
            <td> NO</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>DB</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>MPOSLEFT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 位置</td>
        </tr>
        <tr class="odd">
            <td>MPOSTOP</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 距顶位置</td>
        </tr>
        <tr class="even">
            <td>TYPEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型ID</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>DSTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>MODELTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件类型</td>
        </tr>
        <tr class="even">
            <td>createdata</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建状态</td>
        </tr>
        <tr class="odd">
            <td>startindex</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始位置</td>
        </tr>
        <tr class="even">
            <td>startdate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始时间</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>dataflag</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>CUBEFILE</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型文件</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="立方体分类表">立方体分类表 </h2>
    <table>
        <caption>uk_cube_type 立方体分类表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 维度名称</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户id</td>
        </tr>
        <tr class="even">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型分类上级ID</td>
        </tr>
        <tr class="odd">
            <td>inx</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>description</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="维度成员表">维度成员表 </h2>
    <table>
        <caption>uk_cubelevel 维度成员表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 层级名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 层级代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>timestamp</td>
            <td> 0000-00-00 00:00:00</td>
            <td> NO</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>COLUMNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段名称</td>
        </tr>
        <tr class="even">
            <td>UNIQUEMEMBERS</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 唯一约束</td>
        </tr>
        <tr class="odd">
            <td>TYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>LEVELTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 层级类型</td>
        </tr>
        <tr class="odd">
            <td>TABLENAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表名</td>
        </tr>
        <tr class="even">
            <td>CUBEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 立方体ID</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序序号</td>
        </tr>
        <tr class="odd">
            <td>PARAMETERS</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数</td>
        </tr>
        <tr class="even">
            <td>ATTRIBUE</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 扩展参数</td>
        </tr>
        <tr class="odd">
            <td>DIMID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 唯独ID</td>
        </tr>
        <tr class="even">
            <td>PERMISSIONS</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 权限</td>
        </tr>
        <tr class="odd">
            <td>TABLEPROPERTY</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表字段</td>
        </tr>
        <tr class="even">
            <td>FORMATSTR</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 格式化字符串</td>
        </tr>
        <tr class="odd">
            <td>description</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述信息</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人信息</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="维度指标表">维度指标表 </h2>
    <table>
        <caption>uk_cubemeasure 维度指标表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>timestamp</td>
            <td> 0000-00-00 00:00:00</td>
            <td> NO</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>COLUMNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段名称</td>
        </tr>
        <tr class="even">
            <td>UNIQUEMEMBERS</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 索引约束</td>
        </tr>
        <tr class="odd">
            <td>TYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段类型</td>
        </tr>
        <tr class="even">
            <td>LEVELTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标类型</td>
        </tr>
        <tr class="odd">
            <td>TABLENAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表名称</td>
        </tr>
        <tr class="even">
            <td>CUBEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 立方体ID</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序序号</td>
        </tr>
        <tr class="odd">
            <td>PARAMETERS</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数</td>
        </tr>
        <tr class="even">
            <td>ATTRIBUE</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 扩展属性</td>
        </tr>
        <tr class="odd">
            <td>MID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标ID</td>
        </tr>
        <tr class="even">
            <td>AGGREGATOR</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 授权给用户</td>
        </tr>
        <tr class="odd">
            <td>FORMATSTRING</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 格式化字符串</td>
        </tr>
        <tr class="even">
            <td>CALCULATEDMEMBER</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 计算指标计算方式</td>
        </tr>
        <tr class="odd">
            <td>MODELTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型类型</td>
        </tr>
        <tr class="even">
            <td>MEASURE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标</td>
        </tr>
        <tr class="odd">
            <td>description</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标描述</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="模型元数据表">模型元数据表 </h2>
    <table>
        <caption>uk_cubemetadata 模型元数据表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>TITLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>TB</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>CUBEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型ID</td>
        </tr>
        <tr class="odd">
            <td>POSTOP</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 距顶部位置</td>
        </tr>
        <tr class="even">
            <td>POSLEFT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 距左侧位置</td>
        </tr>
        <tr class="odd">
            <td>MTYPE</td>
            <td>varchar(5)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型类型</td>
        </tr>
        <tr class="even">
            <td>NAMEALIAS</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 别称</td>
        </tr>
        <tr class="odd">
            <td>PARAMETERS</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数</td>
        </tr>
        <tr class="even">
            <td>ATTRIBUE</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 扩展属性</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="报表目录表">报表目录表 </h2>
    <table>
        <caption>uk_datadic 报表目录表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>TITLE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级目录ID</td>
        </tr>
        <tr class="even">
            <td>TYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录类型</td>
        </tr>
        <tr class="odd">
            <td>MEMO</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>CREATER</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>PUBLISHEDTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发布状态</td>
        </tr>
        <tr class="even">
            <td>DESCRIPTION</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="odd">
            <td>TABTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表类型</td>
        </tr>
        <tr class="even">
            <td>DSTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源类型</td>
        </tr>
        <tr class="odd">
            <td>DSTEMPLET</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源模板</td>
        </tr>
        <tr class="even">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序序号</td>
        </tr>
        <tr class="odd">
            <td>DICTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录类型</td>
        </tr>
        <tr class="even">
            <td>ICONCLASS</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 样式CLASS</td>
        </tr>
        <tr class="odd">
            <td>CSSSTYLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 样式STYLE</td>
        </tr>
        <tr class="even">
            <td>AUTHCODE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 认证代码</td>
        </tr>
        <tr class="odd">
            <td>DEFAULTMENU</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认菜单</td>
        </tr>
        <tr class="even">
            <td>DATAID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>DICICON</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图标</td>
        </tr>
        <tr class="even">
            <td>CURICON</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前选中图标</td>
        </tr>
        <tr class="odd">
            <td>BGCOLOR</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 背景颜色</td>
        </tr>
        <tr class="even">
            <td>CURBGCOLOR</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前选中颜色</td>
        </tr>
        <tr class="odd">
            <td>MENUPOS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 菜单位置</td>
        </tr>
        <tr class="even">
            <td>DISTITLE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示标题</td>
        </tr>
        <tr class="odd">
            <td>NAVMENU</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导航菜单</td>
        </tr>
        <tr class="even">
            <td>QUICKMENU</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 快捷方式的菜单</td>
        </tr>
        <tr class="odd">
            <td>PROJECTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 项目ID</td>
        </tr>
        <tr class="even">
            <td>SPSEARCH</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 搜索关键词</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="工单数据变更记录">工单数据变更记录 </h2>
    <table>
        <caption>uk_dataevent 工单数据变更记录</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>tpid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段ID</td>
        </tr>
        <tr class="even">
            <td>propertity</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 属性名称</td>
        </tr>
        <tr class="odd">
            <td>field</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段名称</td>
        </tr>
        <tr class="even">
            <td>newvalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 变更后的值</td>
        </tr>
        <tr class="odd">
            <td>oldvalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 变更前的值</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>modifyid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 变更ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>eventtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件类型</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 变更内容</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="维度表">维度表 </h2>
    <table>
        <caption>uk_dimension 维度表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 维度名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 维度代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CUBEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 立方体ID</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>TYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 维度类型</td>
        </tr>
        <tr class="even">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序序号</td>
        </tr>
        <tr class="odd">
            <td>PARAMETERS</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数</td>
        </tr>
        <tr class="even">
            <td>ATTRIBUE</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 扩展属性</td>
        </tr>
        <tr class="odd">
            <td>POSLEFT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 位置</td>
        </tr>
        <tr class="even">
            <td>POSTOP</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 位置距顶</td>
        </tr>
        <tr class="odd">
            <td>FORMATSTR</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 格式化字符串</td>
        </tr>
        <tr class="even">
            <td>MODELTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型类型</td>
        </tr>
        <tr class="odd">
            <td>DIM</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 维度</td>
        </tr>
        <tr class="even">
            <td>ALLMEMBERNAME</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 小计名称</td>
        </tr>
        <tr class="odd">
            <td>FKFIELD</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键字段</td>
        </tr>
        <tr class="even">
            <td>FKTABLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键表</td>
        </tr>
        <tr class="odd">
            <td>FKTABLEID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键表ID</td>
        </tr>
        <tr class="even">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="钻取表">钻取表 </h2>
    <table>
        <caption>uk_drilldown 钻取表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>memo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>code</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>dataname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据名称</td>
        </tr>
        <tr class="even">
            <td>tdstyle</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> TD样式</td>
        </tr>
        <tr class="odd">
            <td>reportid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表ID</td>
        </tr>
        <tr class="even">
            <td>modelid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型ID</td>
        </tr>
        <tr class="odd">
            <td>paramname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数名称</td>
        </tr>
        <tr class="even">
            <td>paramtype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数类型</td>
        </tr>
        <tr class="odd">
            <td>paramurl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数URL</td>
        </tr>
        <tr class="even">
            <td>paramtarget</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标对象</td>
        </tr>
        <tr class="odd">
            <td>paramreport</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标报表</td>
        </tr>
        <tr class="even">
            <td>paramvalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数值</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="企事业单位信息表">企(事)业单位信息表 </h2>
    <table>
        <caption>uk_entcustomer 企(事)业单位信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 单位ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 企(事)业单位名称</td>
        </tr>
        <tr class="odd">
            <td>etype</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 单位性质</td>
        </tr>
        <tr class="even">
            <td>ekind</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户类别</td>
        </tr>
        <tr class="odd">
            <td>elevel</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户级别</td>
        </tr>
        <tr class="even">
            <td>ecode</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 单位代码</td>
        </tr>
        <tr class="odd">
            <td>nickname</td>
            <td>varchar(64)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 单位简称</td>
        </tr>
        <tr class="even">
            <td>esource</td>
            <td>varchar(64)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="odd">
            <td>origincode</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组织机构代码</td>
        </tr>
        <tr class="even">
            <td>corporation</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 法人</td>
        </tr>
        <tr class="odd">
            <td>leadername</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人姓名</td>
        </tr>
        <tr class="even">
            <td>leadermobile</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人手机</td>
        </tr>
        <tr class="odd">
            <td>leadermobile2</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人手机2</td>
        </tr>
        <tr class="even">
            <td>leaderphone</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人座机</td>
        </tr>
        <tr class="odd">
            <td>leaderemail</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人电子邮件</td>
        </tr>
        <tr class="even">
            <td>website</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 网址</td>
        </tr>
        <tr class="odd">
            <td>email</td>
            <td>varchar(128)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电子邮件</td>
        </tr>
        <tr class="even">
            <td>emailalt</td>
            <td>varchar(128)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用电子邮件</td>
        </tr>
        <tr class="odd">
            <td>phone</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 办公电话</td>
        </tr>
        <tr class="even">
            <td>phonealt</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用办公电话</td>
        </tr>
        <tr class="odd">
            <td>fax</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 传真号码</td>
        </tr>
        <tr class="even">
            <td>faxalt</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用传真号码</td>
        </tr>
        <tr class="odd">
            <td>country</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="even">
            <td>province</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省</td>
        </tr>
        <tr class="odd">
            <td>city</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 市区县</td>
        </tr>
        <tr class="even">
            <td>sarea</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区县</td>
        </tr>
        <tr class="odd">
            <td>address</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地址</td>
        </tr>
        <tr class="even">
            <td>postcode</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮政编码</td>
        </tr>
        <tr class="odd">
            <td>businessscope</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 经营范围</td>
        </tr>
        <tr class="even">
            <td>capital</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 注册资本</td>
        </tr>
        <tr class="odd">
            <td>stockcode</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 股票代码</td>
        </tr>
        <tr class="even">
            <td>bankaccount</td>
            <td>varchar(40)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对公银行账号</td>
        </tr>
        <tr class="odd">
            <td>registeredaddress</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 注册地址</td>
        </tr>
        <tr class="even">
            <td>esize</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 单位规模</td>
        </tr>
        <tr class="odd">
            <td>industry</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属行业</td>
        </tr>
        <tr class="even">
            <td>validstatus</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 效力状态</td>
        </tr>
        <tr class="odd">
            <td>weixin</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信公众号</td>
        </tr>
        <tr class="even">
            <td>weibo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微博号</td>
        </tr>
        <tr class="odd">
            <td>touchtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后联系时间</td>
        </tr>
        <tr class="even">
            <td>dzip</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发货地址邮编</td>
        </tr>
        <tr class="odd">
            <td>daddress</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发货地址</td>
        </tr>
        <tr class="even">
            <td>darea</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发货地址-区县</td>
        </tr>
        <tr class="odd">
            <td>dcity</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发货地址-城市</td>
        </tr>
        <tr class="even">
            <td>dprovince</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发货地址-省份</td>
        </tr>
        <tr class="odd">
            <td>datastatus</td>
            <td>varchar(2)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>processid</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="odd">
            <td>description</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varbinary(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人姓名</td>
        </tr>
        <tr class="even">
            <td>updateuser</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人ID</td>
        </tr>
        <tr class="odd">
            <td>updateusername</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人姓名</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户标识</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配目标用户</td>
        </tr>
        <tr class="even">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="odd">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="even">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属人</td>
        </tr>
        <tr class="odd">
            <td>datadept</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人部门</td>
        </tr>
        <tr class="even">
            <td>batid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="odd">
            <td>maturity</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 规模</td>
        </tr>
        <tr class="even">
            <td>entcusid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户ID</td>
        </tr>
        <tr class="odd">
            <td>pinyin</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 首字母缩写拼音</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="粉丝表">粉丝表 </h2>
    <table>
        <caption>uk_fans 粉丝表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>date</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>date</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>suser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="收藏信息表">收藏信息表 </h2>
    <table>
        <caption>uk_favorites 收藏信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>ORDERID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>TITLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>MODEL</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属组件</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="fs服务器监听端口">fs服务器监听端口 </h2>
    <table>
        <caption>uk_fs_event_socket fs服务器监听端口</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td>是</td>
            <td> 主键</td>
        </tr>
        <tr class="even">
            <td>hostname</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> fs服务名称</td>
        </tr>
        <tr class="odd">
            <td>nat_map</td>
            <td>varchar(20)</td>
            <td> false</td>
            <td> YES</td>
            <td></td>
            <td> nat_map</td>
        </tr>
        <tr class="even">
            <td>listen_ip</td>
            <td>varchar(50)</td>
            <td> 0.0.0.0</td>
            <td> YES</td>
            <td></td>
            <td> listen_ip</td>
        </tr>
        <tr class="odd">
            <td>listen_port</td>
            <td>int(11)</td>
            <td> 8021</td>
            <td> YES</td>
            <td></td>
            <td> listen_port</td>
        </tr>
        <tr class="even">
            <td>password</td>
            <td>varchar(50)</td>
            <td> ClueCon</td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="odd">
            <td>apply_inbound_acl</td>
            <td>varchar(50)</td>
            <td> lan</td>
            <td> YES</td>
            <td></td>
            <td> 呼入ACL</td>
        </tr>
        <tr class="even">
            <td>stop_on_bind_error</td>
            <td>varchar(50)</td>
            <td> true</td>
            <td> YES</td>
            <td></td>
            <td> 错误消息</td>
        </tr>
        <tr class="odd">
            <td>addtime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>connected</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> connected 0-未连接 1-已连接 2 已停止</td>
        </tr>
        <tr class="even">
            <td>connected_result</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 连接结果</td>
        </tr>
        <tr class="odd">
            <td>show_calls</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 容许通话数</td>
        </tr>
        <tr class="even">
            <td>enable</td>
            <td>int(11)</td>
            <td> 1</td>
            <td> YES</td>
            <td></td>
            <td> 是否启用</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="全局id生成器">全局ID生成器 </h2>
    <table>
        <caption>uk_generation 全局ID生成器</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>model</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属组件</td>
        </tr>
        <tr class="odd">
            <td>startinx</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始位置</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="数据采集历史表">数据采集历史表 </h2>
    <table>
        <caption>uk_historyreport 数据采集历史表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>BYTES</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 传输数据量</td>
        </tr>
        <tr class="odd">
            <td>THREADS</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 线程数量</td>
        </tr>
        <tr class="even">
            <td>TYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>ERRORMSG</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 错误提示</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>STARTTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始时间</td>
        </tr>
        <tr class="odd">
            <td>ENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束时间</td>
        </tr>
        <tr class="even">
            <td>AMOUNT</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 提醒</td>
        </tr>
        <tr class="odd">
            <td>PAGES</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 记录数量</td>
        </tr>
        <tr class="even">
            <td>ERRORS</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 错误数</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>TABLEDIRID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表目录ID</td>
        </tr>
        <tr class="odd">
            <td>TABLEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表ID</td>
        </tr>
        <tr class="even">
            <td>TOTAL</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 总数</td>
        </tr>
        <tr class="odd">
            <td>USERID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>organ</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组织机构</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="企业im分组表">企业IM分组表 </h2>
    <table>
        <caption>uk_imgroup 企业IM分组表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>tipmessage</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 提示消息</td>
        </tr>
        <tr class="even">
            <td>descript</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分组名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="企业im分组用户">企业IM分组用户 </h2>
    <table>
        <caption>uk_imgroup_user 企业IM分组用户</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>user_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>imgroup_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分组ID</td>
        </tr>
        <tr class="odd">
            <td>admin</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否管理员</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="微信指令表">微信指令表 </h2>
    <table>
        <caption>uk_instruction 微信指令表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(96)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>code</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编码</td>
        </tr>
        <tr class="even">
            <td>plugin</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 插件</td>
        </tr>
        <tr class="odd">
            <td>memo</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>parent</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级指令</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>scope</td>
            <td>varchar(15)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 作用域</td>
        </tr>
        <tr class="even">
            <td>tipdefault</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认提示</td>
        </tr>
        <tr class="odd">
            <td>matcherule</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 匹配规则</td>
        </tr>
        <tr class="even">
            <td>userbind</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户绑定</td>
        </tr>
        <tr class="odd">
            <td>interfacetype</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接口类型</td>
        </tr>
        <tr class="even">
            <td>adapter</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 适配程序</td>
        </tr>
        <tr class="odd">
            <td>interfaceurl</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接口URL</td>
        </tr>
        <tr class="even">
            <td>interfaceparam</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接口参数</td>
        </tr>
        <tr class="odd">
            <td>messagetype</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息类型</td>
        </tr>
        <tr class="even">
            <td>keyword</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="odd">
            <td>eventype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 菜单事件类型</td>
        </tr>
        <tr class="even">
            <td>snsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="邀请记录表">邀请记录表 </h2>
    <table>
        <caption>uk_inviterecord 邀请记录表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>agentno</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>result</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 记录</td>
        </tr>
        <tr class="even">
            <td>responsetime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 响应时间</td>
        </tr>
        <tr class="odd">
            <td>appid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>url</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地址</td>
        </tr>
        <tr class="even">
            <td>traceid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 跟踪ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="作业调度任务活动批次表">作业调度任务/活动/批次表 </h2>
    <table>
        <caption>uk_jobdetail 作业调度任务/活动/批次表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务状态</td>
        </tr>
        <tr class="even">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>ACTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>NAMENUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的名单总数</td>
        </tr>
        <tr class="even">
            <td>VALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的有效名单总数</td>
        </tr>
        <tr class="odd">
            <td>INVALIDNUM</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 批次包含的无效名单总数</td>
        </tr>
        <tr class="even">
            <td>ASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 已分配名单总数</td>
        </tr>
        <tr class="odd">
            <td>NOTASSIGNED</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 未分配名单总数</td>
        </tr>
        <tr class="even">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> NO</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="odd">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>AREA</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        <tr class="odd">
            <td>imptype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 导入类型</td>
        </tr>
        <tr class="even">
            <td>batchtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次类型</td>
        </tr>
        <tr class="odd">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="even">
            <td>impurl</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 导入URL</td>
        </tr>
        <tr class="odd">
            <td>filetype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件类型</td>
        </tr>
        <tr class="even">
            <td>dbtype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库类型</td>
        </tr>
        <tr class="odd">
            <td>jdbcurl</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库URL</td>
        </tr>
        <tr class="even">
            <td>driverclazz</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库驱动</td>
        </tr>
        <tr class="odd">
            <td>password</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="even">
            <td>DESCRIPTION</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述信息</td>
        </tr>
        <tr class="odd">
            <td>execnum</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导入次数</td>
        </tr>
        <tr class="even">
            <td>SOURCE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="odd">
            <td>CLAZZ</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行的Resource类</td>
        </tr>
        <tr class="even">
            <td>TASKFIRETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启动时间</td>
        </tr>
        <tr class="odd">
            <td>CRAWLTASKID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务ID</td>
        </tr>
        <tr class="even">
            <td>EMAIL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮件地址</td>
        </tr>
        <tr class="odd">
            <td>NICKNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 昵称</td>
        </tr>
        <tr class="even">
            <td>USERID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>TASKTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务类型</td>
        </tr>
        <tr class="even">
            <td>TASKID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务ID</td>
        </tr>
        <tr class="odd">
            <td>FETCHER</td>
            <td>smallint(6)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 当前状态</td>
        </tr>
        <tr class="even">
            <td>PAUSE</td>
            <td>smallint(6)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 暂停</td>
        </tr>
        <tr class="odd">
            <td>PLANTASK</td>
            <td>smallint(6)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 计划任务</td>
        </tr>
        <tr class="even">
            <td>SECURE_ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 安全规则ID</td>
        </tr>
        <tr class="odd">
            <td>CONFIGURE_ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 配置项ID</td>
        </tr>
        <tr class="even">
            <td>TAKSPLAN_ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 计划任务ID</td>
        </tr>
        <tr class="odd">
            <td>CRAWLTASK</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前任务状态</td>
        </tr>
        <tr class="even">
            <td>TARGETTASK</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标任务</td>
        </tr>
        <tr class="odd">
            <td>STARTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始位置</td>
        </tr>
        <tr class="even">
            <td>LASTDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后更新时间</td>
        </tr>
        <tr class="odd">
            <td>CREATETABLE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否创建数据表</td>
        </tr>
        <tr class="even">
            <td>MEMO</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>NEXTFIRETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 下次启动时间</td>
        </tr>
        <tr class="even">
            <td>CRONEXP</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> CRON表达式</td>
        </tr>
        <tr class="odd">
            <td>TASKSTATUS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务执行状态</td>
        </tr>
        <tr class="even">
            <td>usearea</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户数据</td>
        </tr>
        <tr class="odd">
            <td>areafield</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户字段</td>
        </tr>
        <tr class="even">
            <td>areafieldtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户数据类型</td>
        </tr>
        <tr class="odd">
            <td>arearule</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户数据值</td>
        </tr>
        <tr class="even">
            <td>minareavalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最小值</td>
        </tr>
        <tr class="odd">
            <td>maxareavalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最大值</td>
        </tr>
        <tr class="even">
            <td>formatstr</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 格式化字符串</td>
        </tr>
        <tr class="odd">
            <td>DATAID</td>
            <td>varchar(1000)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表id字符串</td>
        </tr>
        <tr class="even">
            <td>DICID</td>
            <td>varchar(1000)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录id字符串</td>
        </tr>
        <tr class="odd">
            <td>taskinfo</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> taskinfo信息</td>
        </tr>
        <tr class="even">
            <td>FILTERID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选表单ID</td>
        </tr>
        <tr class="odd">
            <td>FETCH_SIZE</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 采集数据窗口大小</td>
        </tr>
        <tr class="even">
            <td>LASTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束位置</td>
        </tr>
        <tr class="odd">
            <td>PAGES</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 页面数量</td>
        </tr>
        <tr class="even">
            <td>plantaskreadtorun</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 计划任务</td>
        </tr>
        <tr class="odd">
            <td>priority</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 策略</td>
        </tr>
        <tr class="even">
            <td>runserver</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 运行服务器</td>
        </tr>
        <tr class="odd">
            <td>actype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动类型</td>
        </tr>
        <tr class="even">
            <td>distype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配类型</td>
        </tr>
        <tr class="odd">
            <td>distpolicy</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配策略</td>
        </tr>
        <tr class="even">
            <td>policynum</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配数量</td>
        </tr>
        <tr class="odd">
            <td>busstype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 业务类型</td>
        </tr>
        <tr class="even">
            <td>disnum</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认分配数量</td>
        </tr>
        <tr class="odd">
            <td>execmd</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行次数</td>
        </tr>
        <tr class="even">
            <td>exectarget</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行分配目标</td>
        </tr>
        <tr class="odd">
            <td>exectype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行类型</td>
        </tr>
        <tr class="even">
            <td>execto</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回收数据位置</td>
        </tr>
        <tr class="odd">
            <td>threads</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启动任务的线程数量</td>
        </tr>
        <tr class="even">
            <td>siptrunk</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 线路信息</td>
        </tr>
        <tr class="odd">
            <td>province</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 线路所在省份</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 线路所在城市</td>
        </tr>
        <tr class="odd">
            <td>prefix</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 线路资源拨号前缀</td>
        </tr>
        <tr class="even">
            <td>reportid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表ID</td>
        </tr>
        <tr class="odd">
            <td>mapping</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 默认映射结构</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="活动产品表">活动产品表 </h2>
    <table>
        <caption>uk_jobdetailproduct 活动产品表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>actid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        <tr class="odd">
            <td>product_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 产品ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>quota</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 配额</td>
        </tr>
        <tr class="even">
            <td>price</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 价格</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="知识库">知识库 </h2>
    <table>
        <caption>uk_kbs_expert 知识库</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>user_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>kbstype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库分类</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="知识库内容表">知识库内容表 </h2>
    <table>
        <caption>uk_kbs_topic 知识库内容表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>sessionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主题</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库内容</td>
        </tr>
        <tr class="odd">
            <td>keyword</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="even">
            <td>summary</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 摘要</td>
        </tr>
        <tr class="odd">
            <td>anonymous</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许匿名访问</td>
        </tr>
        <tr class="even">
            <td>begintime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期开始时间</td>
        </tr>
        <tr class="odd">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期结束时间</td>
        </tr>
        <tr class="even">
            <td>top</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否置顶</td>
        </tr>
        <tr class="odd">
            <td>essence</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 精华</td>
        </tr>
        <tr class="even">
            <td>accept</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许评论</td>
        </tr>
        <tr class="odd">
            <td>finish</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 已结束</td>
        </tr>
        <tr class="even">
            <td>answers</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答数量</td>
        </tr>
        <tr class="odd">
            <td>sviews</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 预览次数</td>
        </tr>
        <tr class="even">
            <td>followers</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关注人数</td>
        </tr>
        <tr class="odd">
            <td>collections</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用次数</td>
        </tr>
        <tr class="even">
            <td>comments</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复数</td>
        </tr>
        <tr class="odd">
            <td>mobile</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 移动端支持</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>tptype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类ID</td>
        </tr>
        <tr class="even">
            <td>cate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类ID</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>price</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 权重</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组织机构</td>
        </tr>
        <tr class="odd">
            <td>sms</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 短信模板</td>
        </tr>
        <tr class="even">
            <td>tts</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> TTS模板</td>
        </tr>
        <tr class="odd">
            <td>email</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮件模板</td>
        </tr>
        <tr class="even">
            <td>weixin</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信回复模板</td>
        </tr>
        <tr class="odd">
            <td>tags</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签</td>
        </tr>
        <tr class="even">
            <td>attachment</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 附件</td>
        </tr>
        <tr class="odd">
            <td>approval</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否审批通过</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="知识分类表">知识分类表 </h2>
    <table>
        <caption>uk_kbs_type 知识分类表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人姓名</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库分类上级ID</td>
        </tr>
        <tr class="even">
            <td>APPROVAL</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否启用审批</td>
        </tr>
        <tr class="odd">
            <td>BPMID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 审批流程ID</td>
        </tr>
        <tr class="even">
            <td>PC</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 负责人</td>
        </tr>
        <tr class="odd">
            <td>INX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="even">
            <td>STARTDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期开始时间</td>
        </tr>
        <tr class="odd">
            <td>ENDDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期结束时间</td>
        </tr>
        <tr class="even">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        <tr class="even">
            <td>BPM</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否需要流程审批</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="留言信息表">留言信息表 </h2>
    <table>
        <caption>uk_leavemsg 留言信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 姓名</td>
        </tr>
        <tr class="odd">
            <td>mobile</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 手机</td>
        </tr>
        <tr class="even">
            <td>email</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮件</td>
        </tr>
        <tr class="odd">
            <td>address</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地址</td>
        </tr>
        <tr class="even">
            <td>qq</td>
            <td>varchar(30)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> QQ</td>
        </tr>
        <tr class="odd">
            <td>content</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 留言内容</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>msgstatus</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息状态</td>
        </tr>
        <tr class="even">
            <td>contactsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 匹配联系人ID</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="日志表">日志表 </h2>
    <table>
        <caption>uk_log 日志表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>flowid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 页面ID</td>
        </tr>
        <tr class="even">
            <td>logtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志类型</td>
        </tr>
        <tr class="odd">
            <td>createdate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>msg</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志消息</td>
        </tr>
        <tr class="odd">
            <td>LEVELS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志级别</td>
        </tr>
        <tr class="even">
            <td>thread</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 线程</td>
        </tr>
        <tr class="odd">
            <td>clazz</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> JAVA类</td>
        </tr>
        <tr class="even">
            <td>FILES</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件位置</td>
        </tr>
        <tr class="odd">
            <td>linenumber</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 行号</td>
        </tr>
        <tr class="even">
            <td>method</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 方法名称</td>
        </tr>
        <tr class="odd">
            <td>startid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启动ID</td>
        </tr>
        <tr class="even">
            <td>errorinfo</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 错误消息</td>
        </tr>
        <tr class="odd">
            <td>triggerwarning</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 触发警告</td>
        </tr>
        <tr class="even">
            <td>triggertime</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 警告时间</td>
        </tr>
        <tr class="odd">
            <td>triggertimes</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 警告次数</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志名称</td>
        </tr>
        <tr class="odd">
            <td>code</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志代码</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注信息</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志触发名</td>
        </tr>
        <tr class="odd">
            <td>logtime</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志时间</td>
        </tr>
        <tr class="even">
            <td>ipaddr</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务器地址</td>
        </tr>
        <tr class="odd">
            <td>port</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务器端口</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="请求记录表">请求记录表 </h2>
    <table>
        <caption>uk_log_request 请求记录表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td> 主键ID</td>
            <td> NO</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>parameters</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参数</td>
        </tr>
        <tr class="even">
            <td>throwable</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 异常消息</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>usermail</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户信息</td>
        </tr>
        <tr class="odd">
            <td>filename</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件名</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>error</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 错误信息</td>
        </tr>
        <tr class="even">
            <td>classname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类名</td>
        </tr>
        <tr class="odd">
            <td>starttime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行的开始时间</td>
        </tr>
        <tr class="even">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行的结束时间</td>
        </tr>
        <tr class="odd">
            <td>detailtype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 详情类型</td>
        </tr>
        <tr class="even">
            <td>url</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问URL</td>
        </tr>
        <tr class="odd">
            <td>reportdic</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表目录</td>
        </tr>
        <tr class="even">
            <td>reportname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表名称</td>
        </tr>
        <tr class="odd">
            <td>ip</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP地址</td>
        </tr>
        <tr class="even">
            <td>hostname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机名称</td>
        </tr>
        <tr class="odd">
            <td>statues</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>methodname</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 方法名称</td>
        </tr>
        <tr class="odd">
            <td>linenumber</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 行号</td>
        </tr>
        <tr class="even">
            <td>querytime</td>
            <td>int(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 执行时间</td>
        </tr>
        <tr class="odd">
            <td>optext</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作代码</td>
        </tr>
        <tr class="even">
            <td>field6</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用字段</td>
        </tr>
        <tr class="odd">
            <td>field7</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用字段</td>
        </tr>
        <tr class="even">
            <td>field8</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用字段</td>
        </tr>
        <tr class="odd">
            <td>flowid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 页面ID</td>
        </tr>
        <tr class="even">
            <td>userid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>funtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 方法类型</td>
        </tr>
        <tr class="odd">
            <td>fundesc</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 方法描述</td>
        </tr>
        <tr class="even">
            <td>triggerwarning</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 触发警告</td>
        </tr>
        <tr class="odd">
            <td>triggertime</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 触发时间</td>
        </tr>
        <tr class="even">
            <td>createdate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="个人消息表">个人消息表 </h2>
    <table>
        <caption>uk_message 个人消息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息内容</td>
        </tr>
        <tr class="odd">
            <td>status</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>fromuser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源用户</td>
        </tr>
        <tr class="odd">
            <td>touser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标用户</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="在线访客信息表">在线访客信息表 </h2>
    <table>
        <caption>uk_onlineuser 在线访客信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 分配给目标</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>datastatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间状态</td>
        </tr>
        <tr class="even">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="odd">
            <td>impid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="even">
            <td>ipcode</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP编码</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属用户</td>
        </tr>
        <tr class="odd">
            <td>processid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="even">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>updateuser</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新用户</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="odd">
            <td>resolution</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分辨率</td>
        </tr>
        <tr class="even">
            <td>opersystem</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作系统</td>
        </tr>
        <tr class="odd">
            <td>ip</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP</td>
        </tr>
        <tr class="even">
            <td>hostname</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机名称</td>
        </tr>
        <tr class="odd">
            <td>browser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 浏览器</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(52)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>logintime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问时间</td>
        </tr>
        <tr class="odd">
            <td>sessionid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>usertype</td>
            <td>varchar(52)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客类型</td>
        </tr>
        <tr class="even">
            <td>optype</td>
            <td>varchar(52)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作类型</td>
        </tr>
        <tr class="odd">
            <td>mobile</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 移动端</td>
        </tr>
        <tr class="even">
            <td>phone</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电话号</td>
        </tr>
        <tr class="odd">
            <td>olduser</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 老用户</td>
        </tr>
        <tr class="even">
            <td>country</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客国家</td>
        </tr>
        <tr class="odd">
            <td>region</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客区域</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>isp</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入运营商</td>
        </tr>
        <tr class="even">
            <td>province</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="odd">
            <td>betweentime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 停留时间</td>
        </tr>
        <tr class="even">
            <td>datestr</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间</td>
        </tr>
        <tr class="odd">
            <td>keyword</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 搜索引擎关键词</td>
        </tr>
        <tr class="even">
            <td>source</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>url</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源URL</td>
        </tr>
        <tr class="odd">
            <td>useragent</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> UA</td>
        </tr>
        <tr class="even">
            <td>invitetimes</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 要求次数</td>
        </tr>
        <tr class="odd">
            <td>invitestatus</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请状态</td>
        </tr>
        <tr class="even">
            <td>refusetimes</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 拒绝次数</td>
        </tr>
        <tr class="odd">
            <td>channel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="even">
            <td>appid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="odd">
            <td>contactsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="在线访客访问历史表">在线访客访问历史表 </h2>
    <table>
        <caption>uk_onlineuser_his 在线访客访问历史表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 分配给目标</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>datastatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间状态</td>
        </tr>
        <tr class="even">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="odd">
            <td>impid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="even">
            <td>ipcode</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP编码</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属用户</td>
        </tr>
        <tr class="odd">
            <td>processid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="even">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>updateuser</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新用户</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="odd">
            <td>resolution</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分辨率</td>
        </tr>
        <tr class="even">
            <td>opersystem</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作系统</td>
        </tr>
        <tr class="odd">
            <td>ip</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP</td>
        </tr>
        <tr class="even">
            <td>hostname</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机名称</td>
        </tr>
        <tr class="odd">
            <td>browser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 浏览器</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(52)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>logintime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问时间</td>
        </tr>
        <tr class="odd">
            <td>sessionid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>usertype</td>
            <td>varchar(52)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客类型</td>
        </tr>
        <tr class="even">
            <td>optype</td>
            <td>varchar(52)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作类型</td>
        </tr>
        <tr class="odd">
            <td>mobile</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 移动端</td>
        </tr>
        <tr class="even">
            <td>olduser</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 老用户</td>
        </tr>
        <tr class="odd">
            <td>country</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客国家</td>
        </tr>
        <tr class="even">
            <td>region</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客区域</td>
        </tr>
        <tr class="odd">
            <td>city</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="even">
            <td>isp</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入运营商</td>
        </tr>
        <tr class="odd">
            <td>province</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="even">
            <td>betweentime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 停留时间</td>
        </tr>
        <tr class="odd">
            <td>datestr</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间</td>
        </tr>
        <tr class="even">
            <td>keyword</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 搜索引擎关键词</td>
        </tr>
        <tr class="odd">
            <td>source</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>url</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源URL</td>
        </tr>
        <tr class="even">
            <td>useragent</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> UA</td>
        </tr>
        <tr class="odd">
            <td>invitetimes</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 要求次数</td>
        </tr>
        <tr class="even">
            <td>invitestatus</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邀请状态</td>
        </tr>
        <tr class="odd">
            <td>refusetimes</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 拒绝次数</td>
        </tr>
        <tr class="even">
            <td>channel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="odd">
            <td>appid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="even">
            <td>contactsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关联的OnlineUser数据ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="工单评论回复表">工单评论/回复表 </h2>
    <table>
        <caption>uk_orderscomment 工单评论/回复表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>DATAID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据记录ID</td>
        </tr>
        <tr class="even">
            <td>CONTENT</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复内容</td>
        </tr>
        <tr class="odd">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>OPTIMAL</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作次数</td>
        </tr>
        <tr class="odd">
            <td>PRIREP</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否私有回复</td>
        </tr>
        <tr class="even">
            <td>UP</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 点赞</td>
        </tr>
        <tr class="odd">
            <td>COMMENTS</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复数量</td>
        </tr>
        <tr class="even">
            <td>ADMIN</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否管理员</td>
        </tr>
        <tr class="odd">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>CATE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类</td>
        </tr>
        <tr class="even">
            <td>OPTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作类型</td>
        </tr>
        <tr class="odd">
            <td>IPCODE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP编码</td>
        </tr>
        <tr class="even">
            <td>COUNTRY</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="odd">
            <td>PROVINCE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="even">
            <td>CITY</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>ISP</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 运营商</td>
        </tr>
        <tr class="even">
            <td>REGION</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 地区</td>
        </tr>
        <tr class="odd">
            <td>ROWCOUNT</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编号</td>
        </tr>
        <tr class="even">
            <td>KEY</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="odd">
            <td>APPROVAL</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 审批</td>
        </tr>
        <tr class="even">
            <td>RETBACK</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 退回</td>
        </tr>
        <tr class="odd">
            <td>ACCDEPT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 转办部门</td>
        </tr>
        <tr class="even">
            <td>ACCUSER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 转办用户</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="部门表">部门表 </h2>
    <table>
        <caption>uk_organ 部门表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>ORGID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 企业ID</td>
        </tr>
        <tr class="odd">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>PARENT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 父级ID</td>
        </tr>
        <tr class="odd">
            <td>SKILL</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用技能组</td>
        </tr>
        <tr class="even">
            <td>area</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="企业信息表">企业信息表 </h2>
    <table>
        <caption>uk_organization 企业信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 机构ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>orgtype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>orgscale</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 规模</td>
        </tr>
        <tr class="odd">
            <td>orgindustry</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 行业</td>
        </tr>
        <tr class="even">
            <td>code</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>memo</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>logo</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> LOGO URL</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="部门授权表">部门授权表 </h2>
    <table>
        <caption>uk_organrole 部门授权表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>organ_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机构ID</td>
        </tr>
        <tr class="odd">
            <td>role_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>dicid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录ID</td>
        </tr>
        <tr class="even">
            <td>dicvalue</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录名称</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="租户角色映射表">租户角色映射表 </h2>
    <table>
        <caption>uk_orgi_skill_rel 租户角色映射表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>SKILLID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="字段变更记录">字段变更记录 </h2>
    <table>
        <caption>uk_propertiesevent 字段变更记录</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>tpid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段ID</td>
        </tr>
        <tr class="even">
            <td>propertity</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段</td>
        </tr>
        <tr class="odd">
            <td>field</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 阻断名称</td>
        </tr>
        <tr class="even">
            <td>newvalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 变更后的值</td>
        </tr>
        <tr class="odd">
            <td>oldvalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 变更前的值</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>modifyid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据记录ID</td>
        </tr>
        <tr class="odd">
            <td>textvalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文本值</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="已发布模型">已发布模型 </h2>
    <table>
        <caption>uk_publishedcube 已发布模型</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>DB</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>MPOSLEFT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 位置</td>
        </tr>
        <tr class="odd">
            <td>MPOSTOP</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 距顶位置</td>
        </tr>
        <tr class="even">
            <td>TYPEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类ID</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>DSTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源类型</td>
        </tr>
        <tr class="odd">
            <td>MODELTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型类型</td>
        </tr>
        <tr class="even">
            <td>createdata</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建数据</td>
        </tr>
        <tr class="odd">
            <td>startindex</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始位置</td>
        </tr>
        <tr class="even">
            <td>startdate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始时间</td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>dataflag</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据标识</td>
        </tr>
        <tr class="odd">
            <td>DATAVERSION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 版本</td>
        </tr>
        <tr class="even">
            <td>CREATER</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>USERID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>CUBECONTENT</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 序列化的模型数据</td>
        </tr>
        <tr class="even">
            <td>DBID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>DICLOCATION</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录位置</td>
        </tr>
        <tr class="even">
            <td>USEREMAIL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户邮件</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="已发布报表">已发布报表 </h2>
    <table>
        <caption>uk_publishedreport 已发布报表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 组件ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>DICID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录ID</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>reporttype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表类型</td>
        </tr>
        <tr class="even">
            <td>startindex</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始位置</td>
        </tr>
        <tr class="odd">
            <td>startdate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始时间</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>dataflag</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据标识</td>
        </tr>
        <tr class="even">
            <td>DATAVERSION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据版本</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>REPORTCONTENT</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表内容</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="质检表">质检表 </h2>
    <table>
        <caption>uk_quality 质检表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库分类上级ID</td>
        </tr>
        <tr class="even">
            <td>STARTDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期开始时间</td>
        </tr>
        <tr class="odd">
            <td>ENDDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期结束时间</td>
        </tr>
        <tr class="even">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="odd">
            <td>SCORE</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 分值</td>
        </tr>
        <tr class="even">
            <td>DESCRIPTION</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        <tr class="odd">
            <td>QUALITYTYPE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检方式</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="问卷调查-问题答案表">问卷调查-问题答案表 </h2>
    <table>
        <caption>uk_que_survey_answer 问卷调查-问题答案表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>questionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问题ID</td>
        </tr>
        <tr class="odd">
            <td>questionname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问题名称</td>
        </tr>
        <tr class="even">
            <td>answer</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问题答案</td>
        </tr>
        <tr class="odd">
            <td>queid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 跳转问题ID</td>
        </tr>
        <tr class="even">
            <td>answerscore</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 答案评分</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>processid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷ID</td>
        </tr>
        <tr class="even">
            <td>correct</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否是正确答案（0正确1不正确）</td>
        </tr>
        <tr class="odd">
            <td>hanguptype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 挂断提示语类型</td>
        </tr>
        <tr class="even">
            <td>hangupmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 挂断提示语文字</td>
        </tr>
        <tr class="odd">
            <td>hangupvoice</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 挂断提示语语音</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="问卷调查表">问卷调查表 </h2>
    <table>
        <caption>uk_que_survey_process 问卷调查表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷名称</td>
        </tr>
        <tr class="odd">
            <td>scene</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷适用场景（机器人呼出/坐席手动）</td>
        </tr>
        <tr class="even">
            <td>welword</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷欢迎语（文字）</td>
        </tr>
        <tr class="odd">
            <td>welvoice</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷欢迎语ID（语音）</td>
        </tr>
        <tr class="even">
            <td>weltype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷欢迎语类型</td>
        </tr>
        <tr class="odd">
            <td>endword</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷结束语（文字）</td>
        </tr>
        <tr class="even">
            <td>endvoice</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷结束语ID（语音）</td>
        </tr>
        <tr class="odd">
            <td>endtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷结束语类型</td>
        </tr>
        <tr class="even">
            <td>totalscore</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 参考评分值</td>
        </tr>
        <tr class="odd">
            <td>score</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否评分（0否1是）</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新人</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>prostatus</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 问卷状态（0未发布1发布）</td>
        </tr>
        <tr class="odd">
            <td>sumscore</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 总评分值</td>
        </tr>
        <tr class="even">
            <td>description</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="问卷调查-问题表">问卷调查-问题表 </h2>
    <table>
        <caption>uk_que_survey_question 问卷调查-问题表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问题名称</td>
        </tr>
        <tr class="odd">
            <td>sortindex</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 问题序号</td>
        </tr>
        <tr class="even">
            <td>quetype</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 问题类型（0选择题1问答题）</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>description</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>score</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问题分值</td>
        </tr>
        <tr class="even">
            <td>processid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问卷ID</td>
        </tr>
        <tr class="odd">
            <td>wvtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型（文字/语音）</td>
        </tr>
        <tr class="even">
            <td>quevoice</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语音ID</td>
        </tr>
        <tr class="odd">
            <td>confirmtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 答案确认语类型</td>
        </tr>
        <tr class="even">
            <td>confirmword</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 答案确认语文字</td>
        </tr>
        <tr class="odd">
            <td>confirmvoice</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 答案确认语语音</td>
        </tr>
        <tr class="even">
            <td>overtimetype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语</td>
        </tr>
        <tr class="odd">
            <td>overtimeword</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语文字</td>
        </tr>
        <tr class="even">
            <td>overtimevoice</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语语音</td>
        </tr>
        <tr class="odd">
            <td>errortype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语</td>
        </tr>
        <tr class="even">
            <td>errorword</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语文字</td>
        </tr>
        <tr class="odd">
            <td>errorvoice</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语语音</td>
        </tr>
        <tr class="even">
            <td>replykeyword</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词重复</td>
        </tr>
        <tr class="odd">
            <td>replytype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复提示类型</td>
        </tr>
        <tr class="even">
            <td>replyword</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复提示语文字</td>
        </tr>
        <tr class="odd">
            <td>replyvoice</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复提示语语音</td>
        </tr>
        <tr class="even">
            <td>replyrepeat</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复确认语-最大重复次数</td>
        </tr>
        <tr class="odd">
            <td>replyoperate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复确认语-到达最大次数的操作（转接trans/挂断/handup）</td>
        </tr>
        <tr class="even">
            <td>replytrans</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复确认语-转接号码</td>
        </tr>
        <tr class="odd">
            <td>replytypeup</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复确认语-转接号码</td>
        </tr>
        <tr class="even">
            <td>replywordup</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复确认语-挂断提示语（文字）</td>
        </tr>
        <tr class="odd">
            <td>replyvoiceup</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重复确认语-挂断提示语（语音ID）</td>
        </tr>
        <tr class="even">
            <td>overtimerepeat</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语-最大重复次数</td>
        </tr>
        <tr class="odd">
            <td>overtimeoperate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语-到达最大次数的操作（转接trans/挂断/handup）</td>
        </tr>
        <tr class="even">
            <td>overtimetrans</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语-转接号码</td>
        </tr>
        <tr class="odd">
            <td>overtimetypeup</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语-挂断提示语类型</td>
        </tr>
        <tr class="even">
            <td>overtimewordup</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语-挂断提示语（文字）</td>
        </tr>
        <tr class="odd">
            <td>overtimevoiceup</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答超时语-挂断提示语（语音ID）</td>
        </tr>
        <tr class="even">
            <td>errorepeat</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语-最大重复次数</td>
        </tr>
        <tr class="odd">
            <td>erroroperate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语-到达最大次数的操作（转接trans/挂断/handup）</td>
        </tr>
        <tr class="even">
            <td>errortrans</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语-转接号码</td>
        </tr>
        <tr class="odd">
            <td>errortypeup</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语-挂断提示语类型</td>
        </tr>
        <tr class="even">
            <td>errorwordup</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语-挂断提示语（文字）</td>
        </tr>
        <tr class="odd">
            <td>errorvoiceup</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答错误语-挂断提示语（语音ID）</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="质检项分类">质检项分类 </h2>
    <table>
        <caption>uk_quick_type 质检项分类</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库分类上级ID</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类排序序号</td>
        </tr>
        <tr class="odd">
            <td>STARTDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期开始时间</td>
        </tr>
        <tr class="even">
            <td>ENDDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期结束时间</td>
        </tr>
        <tr class="odd">
            <td>ENABLE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类状态</td>
        </tr>
        <tr class="even">
            <td>DESCRIPTION</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类描述</td>
        </tr>
        <tr class="odd">
            <td>QUICKTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型（公共/个人）</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="快捷回复表">快捷回复表 </h2>
    <table>
        <caption>uk_quickreply 快捷回复表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>cate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="公共已读消息">公共已读消息 </h2>
    <table>
        <caption>uk_recentuser 公共已读消息</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>user_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>lastmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后一条消息</td>
        </tr>
        <tr class="odd">
            <td>newmsg</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 未读消息数量</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="报表数据表">报表数据表 </h2>
    <table>
        <caption>uk_report 报表数据表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表名称</td>
        </tr>
        <tr class="odd">
            <td>REPORTTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表类型</td>
        </tr>
        <tr class="even">
            <td>TITLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主题</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>OBJECTCOUNT</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模板内容</td>
        </tr>
        <tr class="odd">
            <td>DICID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录ID</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述内容</td>
        </tr>
        <tr class="even">
            <td>HTML</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>REPORTPACKAGE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 路径</td>
        </tr>
        <tr class="even">
            <td>USEACL</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问授权</td>
        </tr>
        <tr class="odd">
            <td>status</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>rolename</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色名称</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>blacklist</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 黑名单列表</td>
        </tr>
        <tr class="odd">
            <td>REPORTCONTENT</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表序列化代码</td>
        </tr>
        <tr class="even">
            <td>reportmodel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表模型</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>reportversion</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 版本</td>
        </tr>
        <tr class="even">
            <td>publishedtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发布状态</td>
        </tr>
        <tr class="odd">
            <td>tabtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 切换方式</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>USEREMAIL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户信息</td>
        </tr>
        <tr class="even">
            <td>CACHE</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否启用缓存</td>
        </tr>
        <tr class="odd">
            <td>EXTPARAM</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 导出参数</td>
        </tr>
        <tr class="even">
            <td>TARGETREPORT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目标报表</td>
        </tr>
        <tr class="odd">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表状态</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>SOURCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源</td>
        </tr>
        <tr class="even">
            <td>VIEWTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 视图类型</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="报表筛选器">报表筛选器 </h2>
    <table>
        <caption>uk_reportfilter 报表筛选器</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="odd">
            <td>dataname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据名称</td>
        </tr>
        <tr class="even">
            <td>modelid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件ID</td>
        </tr>
        <tr class="odd">
            <td>reportid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表ID</td>
        </tr>
        <tr class="even">
            <td>contype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 链接类型</td>
        </tr>
        <tr class="odd">
            <td>filtertype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选器类型</td>
        </tr>
        <tr class="even">
            <td>formatstr</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 格式化字符串</td>
        </tr>
        <tr class="odd">
            <td>convalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据值</td>
        </tr>
        <tr class="even">
            <td>userdefvalue</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认数据</td>
        </tr>
        <tr class="odd">
            <td>valuefiltertype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选器值类型</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>code</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="even">
            <td>valuestr</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据值字符串</td>
        </tr>
        <tr class="odd">
            <td>filterprefix</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 过滤器前缀</td>
        </tr>
        <tr class="even">
            <td>filtersuffix</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 过滤器后缀</td>
        </tr>
        <tr class="odd">
            <td>modeltype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 元素类型</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>funtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 业务类型</td>
        </tr>
        <tr class="even">
            <td>measureid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标ID</td>
        </tr>
        <tr class="odd">
            <td>valuecompare</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 值</td>
        </tr>
        <tr class="even">
            <td>defaultvalue</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        <tr class="odd">
            <td>comparetype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选比较类型</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>cubeid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型ID</td>
        </tr>
        <tr class="even">
            <td>mustvalue</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 必选值</td>
        </tr>
        <tr class="odd">
            <td>groupids</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分组ID</td>
        </tr>
        <tr class="even">
            <td>defaultvaluerule</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        <tr class="odd">
            <td>dimid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 维度ID</td>
        </tr>
        <tr class="even">
            <td>endvalue</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 范围值</td>
        </tr>
        <tr class="odd">
            <td>filtertemplet</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选器模板</td>
        </tr>
        <tr class="even">
            <td>noformatvalue</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 未格式化值</td>
        </tr>
        <tr class="odd">
            <td>startvalue</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 筛选范围值</td>
        </tr>
        <tr class="even">
            <td>sortindex</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序位置</td>
        </tr>
        <tr class="odd">
            <td>cascadeid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用ID</td>
        </tr>
        <tr class="even">
            <td>tableproperty</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据字段ID</td>
        </tr>
        <tr class="odd">
            <td>tableid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表ID</td>
        </tr>
        <tr class="even">
            <td>fieldid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段ID</td>
        </tr>
        <tr class="odd">
            <td>fktableid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键表ID</td>
        </tr>
        <tr class="even">
            <td>filterfieldid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键字段ID</td>
        </tr>
        <tr class="odd">
            <td>isdic</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否目录</td>
        </tr>
        <tr class="even">
            <td>diccode</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示值</td>
        </tr>
        <tr class="odd">
            <td>keyfield</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主键字段</td>
        </tr>
        <tr class="even">
            <td>valuefield</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 取值字段</td>
        </tr>
        <tr class="odd">
            <td>fkfieldid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键字段ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="报表布局项">报表布局项 </h2>
    <table>
        <caption>uk_reportmodel 报表布局项</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>posx</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 位置</td>
        </tr>
        <tr class="odd">
            <td>posy</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 位置</td>
        </tr>
        <tr class="even">
            <td>poswidth</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 宽度</td>
        </tr>
        <tr class="odd">
            <td>posheight</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 高度</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>code</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>reportid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表ID</td>
        </tr>
        <tr class="odd">
            <td>modeltype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 元素类型</td>
        </tr>
        <tr class="even">
            <td>sortindex</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序位置</td>
        </tr>
        <tr class="odd">
            <td>stylestr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 样式</td>
        </tr>
        <tr class="even">
            <td>labeltext</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签</td>
        </tr>
        <tr class="odd">
            <td>cssclassname</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 样式名称</td>
        </tr>
        <tr class="even">
            <td>mposleft</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 原生位置</td>
        </tr>
        <tr class="odd">
            <td>mpostop</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 元素位置</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>exchangerw</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 行列交换</td>
        </tr>
        <tr class="even">
            <td>publishedcubeid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型ID</td>
        </tr>
        <tr class="odd">
            <td>rowdimension</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 行维度</td>
        </tr>
        <tr class="even">
            <td>coldimension</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 列维度</td>
        </tr>
        <tr class="odd">
            <td>measure</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 指标</td>
        </tr>
        <tr class="even">
            <td>dstype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源类型</td>
        </tr>
        <tr class="odd">
            <td>dbtype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据类型</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>objectid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 对象ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>filterstr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 过滤器字符串</td>
        </tr>
        <tr class="even">
            <td>sortstr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序字符串</td>
        </tr>
        <tr class="odd">
            <td>viewtype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 视图类型</td>
        </tr>
        <tr class="even">
            <td>chartemplet</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图表模板</td>
        </tr>
        <tr class="odd">
            <td>chartype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图表类型</td>
        </tr>
        <tr class="even">
            <td>chartdatatype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图表数据类型</td>
        </tr>
        <tr class="odd">
            <td>chart3d</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否3D显示图表</td>
        </tr>
        <tr class="even">
            <td>xtitle</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 列标题</td>
        </tr>
        <tr class="odd">
            <td>ytitle</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 行标题</td>
        </tr>
        <tr class="even">
            <td>charttitle</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图表标题</td>
        </tr>
        <tr class="odd">
            <td>displayborder</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示边框</td>
        </tr>
        <tr class="even">
            <td>bordercolor</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 边框颜色</td>
        </tr>
        <tr class="odd">
            <td>displaydesc</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 倒序显示</td>
        </tr>
        <tr class="even">
            <td>formdisplay</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 显示表单</td>
        </tr>
        <tr class="odd">
            <td>labelstyle</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签</td>
        </tr>
        <tr class="even">
            <td>formname</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 表单名称</td>
        </tr>
        <tr class="odd">
            <td>defaultvalue</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        <tr class="even">
            <td>querytext</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 查询语句</td>
        </tr>
        <tr class="odd">
            <td>tempquey</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 查询语句</td>
        </tr>
        <tr class="even">
            <td>displaytitle</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 显示标题</td>
        </tr>
        <tr class="odd">
            <td>clearzero</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 除零</td>
        </tr>
        <tr class="even">
            <td>titlestr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题字符串</td>
        </tr>
        <tr class="odd">
            <td>width</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 宽度</td>
        </tr>
        <tr class="even">
            <td>height</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 高度</td>
        </tr>
        <tr class="odd">
            <td>widthunit</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 宽度单位</td>
        </tr>
        <tr class="even">
            <td>heightunit</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 高度单位</td>
        </tr>
        <tr class="odd">
            <td>defheight</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 定义高度</td>
        </tr>
        <tr class="even">
            <td>defwidth</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 定义宽度</td>
        </tr>
        <tr class="odd">
            <td>neckwidth</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 吸附宽度</td>
        </tr>
        <tr class="even">
            <td>neckheight</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 吸附高度</td>
        </tr>
        <tr class="odd">
            <td>extparam</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 扩展参数</td>
        </tr>
        <tr class="even">
            <td>marginright</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 距离右侧空白</td>
        </tr>
        <tr class="odd">
            <td>colorstr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 颜色样式</td>
        </tr>
        <tr class="even">
            <td>sstart</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始</td>
        </tr>
        <tr class="odd">
            <td>send</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束</td>
        </tr>
        <tr class="even">
            <td>rowformatstr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 行格式化字符串</td>
        </tr>
        <tr class="odd">
            <td>colformatstr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 列格式化字符串</td>
        </tr>
        <tr class="even">
            <td>publishtype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发布状态</td>
        </tr>
        <tr class="odd">
            <td>editview</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编辑状态</td>
        </tr>
        <tr class="even">
            <td>expandbtm</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 收缩位置</td>
        </tr>
        <tr class="odd">
            <td>expandrgt</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 收缩方式</td>
        </tr>
        <tr class="even">
            <td>curtab</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前页签</td>
        </tr>
        <tr class="odd">
            <td>hiddencolstr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 隐藏样式</td>
        </tr>
        <tr class="even">
            <td>eventstr</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件触发样式</td>
        </tr>
        <tr class="odd">
            <td>dsmodel</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源模型</td>
        </tr>
        <tr class="even">
            <td>html</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>sqldialect</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 方言</td>
        </tr>
        <tr class="even">
            <td>pagesize</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分页尺寸</td>
        </tr>
        <tr class="odd">
            <td>isloadfulldata</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 全量数据</td>
        </tr>
        <tr class="even">
            <td>isexport</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导出</td>
        </tr>
        <tr class="odd">
            <td>selectdata</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 选中数据</td>
        </tr>
        <tr class="even">
            <td>exporttitle</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 导出标题</td>
        </tr>
        <tr class="odd">
            <td>colsize</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 列尺寸</td>
        </tr>
        <tr class="even">
            <td>sorttype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序类型</td>
        </tr>
        <tr class="odd">
            <td>sortname</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序名称</td>
        </tr>
        <tr class="even">
            <td>mid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模型ID</td>
        </tr>
        <tr class="odd">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 父级ID</td>
        </tr>
        <tr class="even">
            <td>templetid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模板ID</td>
        </tr>
        <tr class="odd">
            <td>colspan</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 合并列</td>
        </tr>
        <tr class="even">
            <td>colindex</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 列位置</td>
        </tr>
        <tr class="odd">
            <td>chartcontent</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图标代码</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="角色表">角色表 </h2>
    <table>
        <caption>uk_role 角色表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>ORGID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 企业ID</td>
        </tr>
        <tr class="odd">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="角色授权">角色授权 </h2>
    <table>
        <caption>uk_role_auth 角色授权</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>ROLEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色ID</td>
        </tr>
        <tr class="even">
            <td>DICID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 权限ID</td>
        </tr>
        <tr class="odd">
            <td>DICVALUE</td>
            <td>varchar(30)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 权限代码</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销产品">电销产品 </h2>
    <table>
        <caption>uk_sales_product 电销产品</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="even">
            <td>keyword</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="odd">
            <td>summary</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 摘要</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>tptype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>cate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 产品分类</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人姓名</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>price</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 价格</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>termtype</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 有效期类型（1永久有效，0有效期）</td>
        </tr>
        <tr class="even">
            <td>begintime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期开始时间</td>
        </tr>
        <tr class="odd">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期结束时间</td>
        </tr>
        <tr class="even">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>quota</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 配额</td>
        </tr>
        <tr class="even">
            <td>provoice</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语音介绍</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销产品类型">电销产品类型 </h2>
    <table>
        <caption>uk_sales_product_type 电销产品类型</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级产品分类</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销状态">电销状态 </h2>
    <table>
        <caption>uk_sales_status 电销状态</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态名</td>
        </tr>
        <tr class="odd">
            <td>code</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态代码</td>
        </tr>
        <tr class="even">
            <td>cate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态分类ID</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>memo</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>activityid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="电销状态类型">电销状态类型 </h2>
    <table>
        <caption>uk_sales_status_type 电销状态类型</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        <tr class="even">
            <td>name</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态分类名</td>
        </tr>
        <tr class="odd">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 父级ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>activityid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 活动ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="二次验证信息表">二次验证信息表 </h2>
    <table>
        <caption>uk_secret 二次验证信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>password</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 二次密码</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>model</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属组件</td>
        </tr>
        <tr class="odd">
            <td>enable</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否启用</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="服务小结">服务小结 </h2>
    <table>
        <caption>uk_servicesummary 服务小结</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>agentusername</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席用户名</td>
        </tr>
        <tr class="odd">
            <td>agentno</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席ID</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>times</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务次数</td>
        </tr>
        <tr class="even">
            <td>servicetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务时间</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="even">
            <td>channel</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="odd">
            <td>logindate</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录时间</td>
        </tr>
        <tr class="even">
            <td>servicetype</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务类型</td>
        </tr>
        <tr class="odd">
            <td>reservation</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否预约</td>
        </tr>
        <tr class="even">
            <td>reservtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 预约方式</td>
        </tr>
        <tr class="odd">
            <td>reservtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>email</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电子邮件</td>
        </tr>
        <tr class="odd">
            <td>phonenumber</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电话号码</td>
        </tr>
        <tr class="even">
            <td>summary</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录</td>
        </tr>
        <tr class="odd">
            <td>agentserviceid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>statuseventid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电话记录ID</td>
        </tr>
        <tr class="odd">
            <td>contactsid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="even">
            <td>ani</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫</td>
        </tr>
        <tr class="odd">
            <td>caller</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫发起号码</td>
        </tr>
        <tr class="even">
            <td>called</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫</td>
        </tr>
        <tr class="odd">
            <td>agent</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机号</td>
        </tr>
        <tr class="even">
            <td>process</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>updateuser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>processmemo</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="会话设置">会话设置 </h2>
    <table>
        <caption>uk_sessionconfig 会话设置</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>sessionmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话消息</td>
        </tr>
        <tr class="odd">
            <td>distribution</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席分配策略</td>
        </tr>
        <tr class="even">
            <td>timeoutmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 超时提醒消息</td>
        </tr>
        <tr class="odd">
            <td>retimeoutmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 再次超时提醒消息</td>
        </tr>
        <tr class="even">
            <td>satisfaction</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用满意度调查</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>lastagent</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后服务坐席优先分配</td>
        </tr>
        <tr class="odd">
            <td>sessiontimeout</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话超时时间</td>
        </tr>
        <tr class="even">
            <td>resessiontimeout</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 再次超时时间</td>
        </tr>
        <tr class="odd">
            <td>timeout</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 超时时长</td>
        </tr>
        <tr class="even">
            <td>retimeout</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 再次超时时长</td>
        </tr>
        <tr class="odd">
            <td>agenttimeout</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复超时时长</td>
        </tr>
        <tr class="even">
            <td>agentreplaytimeout</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席回复超时时长</td>
        </tr>
        <tr class="odd">
            <td>agenttimeoutmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 超时回复消息</td>
        </tr>
        <tr class="even">
            <td>maxuser</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最大用户数</td>
        </tr>
        <tr class="odd">
            <td>initmaxuser</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 首次就绪分配用户数</td>
        </tr>
        <tr class="even">
            <td>workinghours</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 工作时间段</td>
        </tr>
        <tr class="odd">
            <td>notinwhmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 非工作时间提醒消息</td>
        </tr>
        <tr class="even">
            <td>hourcheck</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用工作时间</td>
        </tr>
        <tr class="odd">
            <td>noagentmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 无坐席消息提醒</td>
        </tr>
        <tr class="even">
            <td>agentbusymsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席忙提醒</td>
        </tr>
        <tr class="odd">
            <td>successmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配成功提醒</td>
        </tr>
        <tr class="even">
            <td>finessmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束会话提示消息</td>
        </tr>
        <tr class="odd">
            <td>quality</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 质检</td>
        </tr>
        <tr class="even">
            <td>qualityscore</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 质检评分</td>
        </tr>
        <tr class="odd">
            <td>servicetimeoutlimit</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 服务超时提醒</td>
        </tr>
        <tr class="even">
            <td>servicetimeout</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 服务超时提醒消息</td>
        </tr>
        <tr class="odd">
            <td>servicetimeoutmsg</td>
            <td>varchar(50)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 服务超时时间</td>
        </tr>
        <tr class="even">
            <td>quenetimeout</td>
            <td>int(11)</td>
            <td> 600</td>
            <td> YES</td>
            <td></td>
            <td> 允许访客排队的最大时长</td>
        </tr>
        <tr class="odd">
            <td>quenetimeoutmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访客排队超市提示消息</td>
        </tr>
        <tr class="even">
            <td>quene</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席姓名</td>
        </tr>
        <tr class="odd">
            <td>servicename</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 无坐席的时候回复昵称</td>
        </tr>
        <tr class="even">
            <td>agentautoleave</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 关闭浏览器自动离线</td>
        </tr>
        <tr class="odd">
            <td>otherquickplay</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用外部快捷回复功能</td>
        </tr>
        <tr class="even">
            <td>oqrsearchurl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部快捷回复搜索地址</td>
        </tr>
        <tr class="odd">
            <td>oqrsearchinput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部快捷回复搜索输入参数</td>
        </tr>
        <tr class="even">
            <td>oqrsearchoutput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部快捷回复搜索输出参数</td>
        </tr>
        <tr class="odd">
            <td>oqrdetailurl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部快捷回复内容URL</td>
        </tr>
        <tr class="even">
            <td>oqrdetailinput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部快捷回复详情输入参数</td>
        </tr>
        <tr class="odd">
            <td>oqrdetailoutput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部快捷回复详情输出参数</td>
        </tr>
        <tr class="even">
            <td>agentctrlenter</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用坐席端CTRL+Enter发送消息</td>
        </tr>
        <tr class="odd">
            <td>ctrlenter</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用访客端CTRL+Enter发送消息</td>
        </tr>
        <tr class="even">
            <td>enablequick</td>
            <td>tinyint(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用快捷回复功能</td>
        </tr>
        <tr class="odd">
            <td>otherssl</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 外部知识库启用SSL</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="技能组表">技能组表 </h2>
    <table>
        <caption>uk_skill 技能组表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="渠道配置表">渠道配置表 </h2>
    <table>
        <caption>uk_snsaccount 渠道配置表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>authorizeURL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 认证URL</td>
        </tr>
        <tr class="even">
            <td>accessTokenURL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微博TokenURL</td>
        </tr>
        <tr class="odd">
            <td>baseURL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 基础URL</td>
        </tr>
        <tr class="even">
            <td>redirectURI</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 重定向URL</td>
        </tr>
        <tr class="odd">
            <td>clientSERCRET</td>
            <td>varchar(192)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 安全码</td>
        </tr>
        <tr class="even">
            <td>clientID</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户端ID</td>
        </tr>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(96)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>states</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>region</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 账号名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编码</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>APIPOINT</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> API接入点</td>
        </tr>
        <tr class="even">
            <td>PASSWORD</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="odd">
            <td>SNSTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 账号类型（微博/微信）</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>ACCOUNT</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 账号</td>
        </tr>
        <tr class="even">
            <td>ALLOWREMOTE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许远程访问</td>
        </tr>
        <tr class="odd">
            <td>EMAIL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮件</td>
        </tr>
        <tr class="even">
            <td>USERNO</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>TOKEN</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信Token</td>
        </tr>
        <tr class="even">
            <td>APPKEY</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信AppKey</td>
        </tr>
        <tr class="odd">
            <td>SECRET</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信Secret</td>
        </tr>
        <tr class="even">
            <td>AESKEY</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信AesKey</td>
        </tr>
        <tr class="odd">
            <td>APPTOKEN</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信AppToken</td>
        </tr>
        <tr class="even">
            <td>SESSIONKEY</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话Key</td>
        </tr>
        <tr class="odd">
            <td>MOREPARAM</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更多参数</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组织机构ID</td>
        </tr>
        <tr class="even">
            <td>DEFAULTACCOUNT</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认账号</td>
        </tr>
        <tr class="odd">
            <td>lastatupdate</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后更新时间</td>
        </tr>
        <tr class="even">
            <td>lastprimsgupdate</td>
            <td>varchar(96)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>ACCTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 账号类型</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>create_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>update_username</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新用户</td>
        </tr>
        <tr class="even">
            <td>update_time</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="odd">
            <td>update_user</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改人</td>
        </tr>
        <tr class="even">
            <td>shares</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分享给</td>
        </tr>
        <tr class="odd">
            <td>owner</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属人</td>
        </tr>
        <tr class="even">
            <td>assignedto</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分配目标用户</td>
        </tr>
        <tr class="odd">
            <td>wfstatus</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程状态</td>
        </tr>
        <tr class="even">
            <td>datadept</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人部门</td>
        </tr>
        <tr class="odd">
            <td>batid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 批次ID</td>
        </tr>
        <tr class="even">
            <td>alias</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 别称</td>
        </tr>
        <tr class="odd">
            <td>authaccesstoken</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 认证token（微信第三方平台）</td>
        </tr>
        <tr class="even">
            <td>expirestime</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 过期时间（微信第三方平台）</td>
        </tr>
        <tr class="odd">
            <td>headimg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 账号头像（微信第三方平台）</td>
        </tr>
        <tr class="even">
            <td>oepnscan</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 极光推送信息</td>
        </tr>
        <tr class="odd">
            <td>opencard</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 极光推送信息</td>
        </tr>
        <tr class="even">
            <td>openstore</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 极光推送信息</td>
        </tr>
        <tr class="odd">
            <td>openpay</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 极光推送信息</td>
        </tr>
        <tr class="even">
            <td>openshake</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 极光推送信息</td>
        </tr>
        <tr class="odd">
            <td>qrcode</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 二维码</td>
        </tr>
        <tr class="even">
            <td>refreshtoken</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 刷新token</td>
        </tr>
        <tr class="odd">
            <td>verify</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 验证代码</td>
        </tr>
        <tr class="even">
            <td>snsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="odd">
            <td>agent</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="字典表">字典表 </h2>
    <table>
        <caption>uk_sysdic 字典表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字典名称</td>
        </tr>
        <tr class="odd">
            <td>TITLE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>CTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 父级ID</td>
        </tr>
        <tr class="even">
            <td>DESCRIPTION</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="odd">
            <td>MEMO</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>ICONSTR</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图标</td>
        </tr>
        <tr class="odd">
            <td>ICONSKIN</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 自定义样式</td>
        </tr>
        <tr class="even">
            <td>CATETYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>HASCHILD</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否有下级</td>
        </tr>
        <tr class="odd">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序</td>
        </tr>
        <tr class="even">
            <td>DICID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录ID</td>
        </tr>
        <tr class="odd">
            <td>DEFAULTVALUE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        <tr class="even">
            <td>DISCODE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编码</td>
        </tr>
        <tr class="odd">
            <td>URL</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 系统权限资源的URL</td>
        </tr>
        <tr class="even">
            <td>MODULE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 权限资源所属模块</td>
        </tr>
        <tr class="odd">
            <td>MLEVEL</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 菜单级别（一级/二级）</td>
        </tr>
        <tr class="even">
            <td>RULES</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>MENUTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 菜单类型（顶部菜单/左侧菜单）</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="公告信息表">公告信息表 </h2>
    <table>
        <caption>uk_system_message 公告信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>MSGTYPE</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 消息类型</td>
        </tr>
        <tr class="odd">
            <td>SMTPSERVER</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SMTP服务器</td>
        </tr>
        <tr class="even">
            <td>SMTPUSER</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SMTP账号</td>
        </tr>
        <tr class="odd">
            <td>SMTPPASSWORD</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SMTP密码</td>
        </tr>
        <tr class="even">
            <td>MAILFROM</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发件人</td>
        </tr>
        <tr class="odd">
            <td>SECLEV</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用SSL</td>
        </tr>
        <tr class="even">
            <td>SSLPORT</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SSL端口</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>URL</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> URL</td>
        </tr>
        <tr class="odd">
            <td>smstype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 短信类型</td>
        </tr>
        <tr class="even">
            <td>APPKEY</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> APPKEY</td>
        </tr>
        <tr class="odd">
            <td>APPSEC</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> APPSEC</td>
        </tr>
        <tr class="even">
            <td>SIGN</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 签名</td>
        </tr>
        <tr class="odd">
            <td>TPCODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> TP代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮件或短信网关名称</td>
        </tr>
        <tr class="even">
            <td>moreparam</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更多参数</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="系统设置表">系统设置表 </h2>
    <table>
        <caption>uk_systemconfig 系统设置表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>TITLE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 编码</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>CTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 父级ID</td>
        </tr>
        <tr class="even">
            <td>DESCRIPTION</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="odd">
            <td>MEMO</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>ICONSTR</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 自定义样式</td>
        </tr>
        <tr class="odd">
            <td>ICONSKIN</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 自定义样式</td>
        </tr>
        <tr class="even">
            <td>CATETYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>HASCHILD</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否有下级</td>
        </tr>
        <tr class="odd">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序</td>
        </tr>
        <tr class="even">
            <td>DICID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 目录ID</td>
        </tr>
        <tr class="odd">
            <td>DEFAULTVALUE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        <tr class="even">
            <td>THEME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 皮肤</td>
        </tr>
        <tr class="odd">
            <td>LOGLEVEL</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日志级别</td>
        </tr>
        <tr class="even">
            <td>ENABLESSL</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用SSL</td>
        </tr>
        <tr class="odd">
            <td>JKSFILE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> JKS文件路径</td>
        </tr>
        <tr class="even">
            <td>JKSPASSWORD</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> JKS密码</td>
        </tr>
        <tr class="odd">
            <td>MAPKEY</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 百度地图授权编码</td>
        </tr>
        <tr class="even">
            <td>workorders</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用工单三栏布局</td>
        </tr>
        <tr class="odd">
            <td>callcenter</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用呼叫中心</td>
        </tr>
        <tr class="even">
            <td>cc_extention</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机</td>
        </tr>
        <tr class="odd">
            <td>cc_quene</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组队列</td>
        </tr>
        <tr class="even">
            <td>cc_router</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 路由策略</td>
        </tr>
        <tr class="odd">
            <td>cc_ivr</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IVR模板</td>
        </tr>
        <tr class="even">
            <td>cc_acl</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问列表模板</td>
        </tr>
        <tr class="odd">
            <td>cc_siptrunk</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SIP配置模板</td>
        </tr>
        <tr class="even">
            <td>cc_callcenter</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫中心配置</td>
        </tr>
        <tr class="odd">
            <td>CALLOUT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否允许点击号码外呼</td>
        </tr>
        <tr class="even">
            <td>AUTH</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用权限控制</td>
        </tr>
        <tr class="odd">
            <td>enablemail</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用发送邮件</td>
        </tr>
        <tr class="even">
            <td>enablesms</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用发送短信</td>
        </tr>
        <tr class="odd">
            <td>emailid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用AI</td>
        </tr>
        <tr class="even">
            <td>emailworkordertp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用工单邮件发送</td>
        </tr>
        <tr class="odd">
            <td>smsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 短信网关ID</td>
        </tr>
        <tr class="even">
            <td>smsworkordertp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用工单发送短信</td>
        </tr>
        <tr class="odd">
            <td>mailcreatetp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建发送邮件模板</td>
        </tr>
        <tr class="even">
            <td>mailupdatetp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新发送邮件模板</td>
        </tr>
        <tr class="odd">
            <td>mailprocesstp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 处理发送邮件模板</td>
        </tr>
        <tr class="even">
            <td>emailtocreater</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 更新发送创建人邮件模板</td>
        </tr>
        <tr class="odd">
            <td>emailshowrecipient</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 发送邮件给创建人</td>
        </tr>
        <tr class="even">
            <td>smscreatetp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建发送短信</td>
        </tr>
        <tr class="odd">
            <td>smsupdatetp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>smsprocesstp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 处理发送短信</td>
        </tr>
        <tr class="odd">
            <td>smstocreater</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 创建发送短信</td>
        </tr>
        <tr class="even">
            <td>emailtocreatertp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建发送短信模板</td>
        </tr>
        <tr class="odd">
            <td>smstocreatertp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新发送短信模板</td>
        </tr>
        <tr class="even">
            <td>enabletneant</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用多租户</td>
        </tr>
        <tr class="odd">
            <td>tenantshare</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 租户共享数据</td>
        </tr>
        <tr class="even">
            <td>namealias</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 别称</td>
        </tr>
        <tr class="odd">
            <td>enableregorgi</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用注册租户</td>
        </tr>
        <tr class="even">
            <td>tenantconsole</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 登录跳转到租户切换页面</td>
        </tr>
        <tr class="odd">
            <td>loginlogo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登陆页面Logo</td>
        </tr>
        <tr class="even">
            <td>consolelogo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 后台页面Logo</td>
        </tr>
        <tr class="odd">
            <td>favlogo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 系统Fav图标Logo</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="元数据字段表">元数据字段表 </h2>
    <table>
        <caption>uk_tableproperties 元数据字段表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>GROUPID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组ID</td>
        </tr>
        <tr class="odd">
            <td>USERID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>FIELDNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段名称</td>
        </tr>
        <tr class="odd">
            <td>DATATYPECODE</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 数据类型代码</td>
        </tr>
        <tr class="even">
            <td>DATATYPENAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段类型名称</td>
        </tr>
        <tr class="odd">
            <td>DBTABLEID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表ID</td>
        </tr>
        <tr class="even">
            <td>INDEXDATATYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段类型</td>
        </tr>
        <tr class="odd">
            <td>PK</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否外键</td>
        </tr>
        <tr class="even">
            <td>MODITS</td>
            <td>smallint(6)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否启用多值</td>
        </tr>
        <tr class="odd">
            <td>INDEXFIELD</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否索引</td>
        </tr>
        <tr class="even">
            <td>PLUGIN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 处理插件</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>FKTABLE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键表</td>
        </tr>
        <tr class="odd">
            <td>FKPROPERTY</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键字段</td>
        </tr>
        <tr class="even">
            <td>TABLENAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表名称</td>
        </tr>
        <tr class="odd">
            <td>viewtype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 视图类型</td>
        </tr>
        <tr class="even">
            <td>SORTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序位置</td>
        </tr>
        <tr class="odd">
            <td>SYSTEMFIELD</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 系统字段</td>
        </tr>
        <tr class="even">
            <td>INX</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 索引</td>
        </tr>
        <tr class="odd">
            <td>TOKEN</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分词</td>
        </tr>
        <tr class="even">
            <td>LENGTH</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 长度</td>
        </tr>
        <tr class="odd">
            <td>FIELDSTATUS</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段状态</td>
        </tr>
        <tr class="even">
            <td>SELDATA</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关联字段数据</td>
        </tr>
        <tr class="odd">
            <td>SELDATACODE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关联字段代码</td>
        </tr>
        <tr class="even">
            <td>SELDATAKEY</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关联key</td>
        </tr>
        <tr class="odd">
            <td>SELDATAVALUE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关联字段值</td>
        </tr>
        <tr class="even">
            <td>SELDATATYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关联类型</td>
        </tr>
        <tr class="odd">
            <td>REFTBID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用表ID</td>
        </tr>
        <tr class="even">
            <td>REFTPID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用字段ID</td>
        </tr>
        <tr class="odd">
            <td>REFTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用类型</td>
        </tr>
        <tr class="even">
            <td>REFTBNAME</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用表名称</td>
        </tr>
        <tr class="odd">
            <td>REFTPNAME</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用字段名称</td>
        </tr>
        <tr class="even">
            <td>REFTPTITLEFIELD</td>
            <td>varchar(60)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用字段ID</td>
        </tr>
        <tr class="odd">
            <td>REFFK</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外键</td>
        </tr>
        <tr class="even">
            <td>DEFAULTSORT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认排序</td>
        </tr>
        <tr class="odd">
            <td>DEFAULTVALUE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认值</td>
        </tr>
        <tr class="even">
            <td>DEFAULTVALUETITLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认标题</td>
        </tr>
        <tr class="odd">
            <td>DEFAULTFIELDVALUE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认标题内容</td>
        </tr>
        <tr class="even">
            <td>MULTPARTFILE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 多值</td>
        </tr>
        <tr class="odd">
            <td>UPLOADTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 文件上传类型</td>
        </tr>
        <tr class="even">
            <td>cascadetype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>DESCORDER</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序方式</td>
        </tr>
        <tr class="odd">
            <td>impfield</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 导入字段</td>
        </tr>
        <tr class="even">
            <td>tokentype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分词</td>
        </tr>
        <tr class="odd">
            <td>phonenumber</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否电话号码</td>
        </tr>
        <tr class="even">
            <td>phonetype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电话号码类型</td>
        </tr>
        <tr class="odd">
            <td>phonememo</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 电话号码备注</td>
        </tr>
        <tr class="even">
            <td>secfield</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 隐藏字段</td>
        </tr>
        <tr class="odd">
            <td>secdistype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 字段隐藏方式</td>
        </tr>
        <tr class="even">
            <td>styletype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 样式</td>
        </tr>
        <tr class="odd">
            <td>sysfield</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 系统字段</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="元数据信息表">元数据信息表 </h2>
    <table>
        <caption>uk_tabletask 元数据信息表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 元数据表名称</td>
        </tr>
        <tr class="odd">
            <td>SECURE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 安全级别</td>
        </tr>
        <tr class="even">
            <td>TASKSTATUS</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务状态</td>
        </tr>
        <tr class="odd">
            <td>TABLEDIRID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表目录ID</td>
        </tr>
        <tr class="even">
            <td>DBID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源ID</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>GROUPID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分组ID</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>CREATERNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人姓名</td>
        </tr>
        <tr class="odd">
            <td>TASKTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务类型</td>
        </tr>
        <tr class="even">
            <td>TASKNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务名称</td>
        </tr>
        <tr class="odd">
            <td>TASKPLAN</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 任务计划</td>
        </tr>
        <tr class="even">
            <td>CONFIGURE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 配置</td>
        </tr>
        <tr class="odd">
            <td>SECURECONF</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 安全级别</td>
        </tr>
        <tr class="even">
            <td>USERID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>PREVIEWTEMPLET</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 预览模板</td>
        </tr>
        <tr class="even">
            <td>LISTBLOCKTEMPLET</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 详情模板</td>
        </tr>
        <tr class="odd">
            <td>TABLENAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表名称</td>
        </tr>
        <tr class="even">
            <td>TABLETYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据表类型</td>
        </tr>
        <tr class="odd">
            <td>STARTINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 开始位置</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>UPDATETIMENUMBER</td>
            <td>int(11)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 更新时间时间戳</td>
        </tr>
        <tr class="even">
            <td>DATASQL</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SQL</td>
        </tr>
        <tr class="odd">
            <td>DATABASETASK</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库任务</td>
        </tr>
        <tr class="even">
            <td>DRIVERPLUGIN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 驱动</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>WORKFLOW</td>
            <td>tinyint(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程</td>
        </tr>
        <tr class="odd">
            <td>FROMDB</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源数据库</td>
        </tr>
        <tr class="even">
            <td>tabtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 切换类型</td>
        </tr>
        <tr class="odd">
            <td>pid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部ID</td>
        </tr>
        <tr class="even">
            <td>secmenuid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 二级菜单ID</td>
        </tr>
        <tr class="odd">
            <td>reportid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 报表ID</td>
        </tr>
        <tr class="even">
            <td>eventname</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件名称</td>
        </tr>
        <tr class="odd">
            <td>tltemplet</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题模板</td>
        </tr>
        <tr class="even">
            <td>timeline</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间</td>
        </tr>
        <tr class="odd">
            <td>tbversion</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 版本</td>
        </tr>
        <tr class="even">
            <td>LASTUPDATE</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最近更新时间</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="标签表">标签表 </h2>
    <table>
        <caption>uk_tag 标签表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>tag</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>times</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用次数</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>tagtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签类型</td>
        </tr>
        <tr class="even">
            <td>icon</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图标</td>
        </tr>
        <tr class="odd">
            <td>color</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 颜色</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="标签映射关系表">标签映射关系表 </h2>
    <table>
        <caption>uk_tagrelation 标签映射关系表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>tagid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签ID</td>
        </tr>
        <tr class="odd">
            <td>userid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="模板表">模板表 </h2>
    <table>
        <caption>uk_templet 模板表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模板名称</td>
        </tr>
        <tr class="odd">
            <td>DESCRIPTION</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 描述</td>
        </tr>
        <tr class="even">
            <td>CODE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="odd">
            <td>GROUPID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组ID</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>USERID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>TEMPLETTITLE</td>
            <td>varchar(500)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模板标题内容</td>
        </tr>
        <tr class="odd">
            <td>TEMPLETTEXT</td>
            <td>longtext</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模板内容</td>
        </tr>
        <tr class="even">
            <td>TEMPLETTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 模板类型</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>ICONSTR</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 自定义样式</td>
        </tr>
        <tr class="odd">
            <td>MEMO</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>ORDERINDEX</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排序位置</td>
        </tr>
        <tr class="odd">
            <td>TYPEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类ID</td>
        </tr>
        <tr class="even">
            <td>SELDATA</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用外键</td>
        </tr>
        <tr class="odd">
            <td>layoutcols</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 布局列数</td>
        </tr>
        <tr class="even">
            <td>datatype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据类型</td>
        </tr>
        <tr class="odd">
            <td>charttype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 图表类型</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="租户表">租户表 </h2>
    <table>
        <caption>uk_tenant 租户表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>datasourceid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据源ID</td>
        </tr>
        <tr class="odd">
            <td>tenantname</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户名称</td>
        </tr>
        <tr class="even">
            <td>tenantcode</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户代码</td>
        </tr>
        <tr class="odd">
            <td>remark</td>
            <td>varchar(200)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签</td>
        </tr>
        <tr class="even">
            <td>lastmenutime</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 菜单创建时间</td>
        </tr>
        <tr class="odd">
            <td>lastbasetime</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据库创建时间</td>
        </tr>
        <tr class="even">
            <td>tenantlogo</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> LOGO</td>
        </tr>
        <tr class="odd">
            <td>tenantvalid</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 验证</td>
        </tr>
        <tr class="even">
            <td>genpasstype</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 自动生成密码</td>
        </tr>
        <tr class="odd">
            <td>password</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="even">
            <td>adminuser</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 管理员用户</td>
        </tr>
        <tr class="odd">
            <td>orgid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 企业ID</td>
        </tr>
        <tr class="even">
            <td>initdb</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 初始化DB</td>
        </tr>
        <tr class="odd">
            <td>inites</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 初始化ES</td>
        </tr>
        <tr class="even">
            <td>inited</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 初始化完成</td>
        </tr>
        <tr class="odd">
            <td>systemtenant</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 系统租户</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>sign</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 签名</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="用户表">用户表 </h2>
    <table>
        <caption>uk_user 用户表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>LANGUAGE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语言</td>
        </tr>
        <tr class="odd">
            <td>USERNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>PASSWORD</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 密码</td>
        </tr>
        <tr class="odd">
            <td>SECURECONF</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 安全级别</td>
        </tr>
        <tr class="even">
            <td>EMAIL</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮件</td>
        </tr>
        <tr class="odd">
            <td>FIRSTNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 姓</td>
        </tr>
        <tr class="even">
            <td>MIDNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名</td>
        </tr>
        <tr class="odd">
            <td>LASTNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名</td>
        </tr>
        <tr class="even">
            <td>JOBTITLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 职位</td>
        </tr>
        <tr class="odd">
            <td>DEPARTMENT</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="even">
            <td>GENDER</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 性别</td>
        </tr>
        <tr class="odd">
            <td>BIRTHDAY</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 生日</td>
        </tr>
        <tr class="even">
            <td>NICKNAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 昵称</td>
        </tr>
        <tr class="odd">
            <td>USERTYPE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户类型</td>
        </tr>
        <tr class="even">
            <td>RULENAME</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色</td>
        </tr>
        <tr class="odd">
            <td>SEARCHPROJECTID</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备用</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>ORGID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 企业ID</td>
        </tr>
        <tr class="even">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>MEMO</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="even">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>MOBILE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 手机号</td>
        </tr>
        <tr class="even">
            <td>passupdatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后 一次密码修改时间</td>
        </tr>
        <tr class="odd">
            <td>sign</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 签名</td>
        </tr>
        <tr class="even">
            <td>del</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否已删除</td>
        </tr>
        <tr class="odd">
            <td>uname</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 姓名</td>
        </tr>
        <tr class="even">
            <td>musteditpassword</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录修改密码</td>
        </tr>
        <tr class="odd">
            <td>AGENT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 工号</td>
        </tr>
        <tr class="even">
            <td>SKILL</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="odd">
            <td>province</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>fans</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关注人数</td>
        </tr>
        <tr class="even">
            <td>follows</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被关注次数</td>
        </tr>
        <tr class="odd">
            <td>integral</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 积分</td>
        </tr>
        <tr class="even">
            <td>lastlogintime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最后登录时间</td>
        </tr>
        <tr class="odd">
            <td>status</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="even">
            <td>deactivetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 离线时间</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="odd">
            <td>callcenter</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用呼叫中心坐席</td>
        </tr>
        <tr class="even">
            <td>sipaccount</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> sip地址</td>
        </tr>
        <tr class="odd">
            <td>SUPERUSER</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否超级管理员</td>
        </tr>
        <tr class="even">
            <td>maxuser</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 最大接入访客数量</td>
        </tr>
        <tr class="odd">
            <td>ordertype</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 默认排序方式</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="访客浏览记录表">访客浏览记录表 </h2>
    <table>
        <caption>uk_userevent 访客浏览记录表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>maintype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> Spring MVC注释分类</td>
        </tr>
        <tr class="even">
            <td>subtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 注释二级分类</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>admin</td>
            <td>tinyint(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否管理员</td>
        </tr>
        <tr class="odd">
            <td>accessnum</td>
            <td>tinyint(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问次数</td>
        </tr>
        <tr class="even">
            <td>ip</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> IP</td>
        </tr>
        <tr class="odd">
            <td>hostname</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主机名</td>
        </tr>
        <tr class="even">
            <td>country</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="odd">
            <td>region</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>isp</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 运营商</td>
        </tr>
        <tr class="even">
            <td>province</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="odd">
            <td>url</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入URL</td>
        </tr>
        <tr class="even">
            <td>sessionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>param</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 请求参数</td>
        </tr>
        <tr class="even">
            <td>times</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问次数</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 访问时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 页面标题</td>
        </tr>
        <tr class="even">
            <td>ostype</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作系统</td>
        </tr>
        <tr class="odd">
            <td>browser</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 浏览器</td>
        </tr>
        <tr class="even">
            <td>mobile</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 移动端</td>
        </tr>
        <tr class="odd">
            <td>model</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件</td>
        </tr>
        <tr class="even">
            <td>appid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="odd">
            <td>createdate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>referer</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 跳转URL</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="用户角色">用户角色 </h2>
    <table>
        <caption>uk_userrole 用户角色</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>user_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户ID</td>
        </tr>
        <tr class="odd">
            <td>role_id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 角色ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="坐席状态表">坐席状态表 </h2>
    <table>
        <caption>uk_webim_monitor 坐席状态表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> ID</td>
        </tr>
        <tr class="even">
            <td>ORGI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 记录创建时间</td>
        </tr>
        <tr class="even">
            <td>AGENTS</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 在线坐席数量</td>
        </tr>
        <tr class="odd">
            <td>USERS</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 咨询中访客数量</td>
        </tr>
        <tr class="even">
            <td>INQUENE</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 排队访客数量</td>
        </tr>
        <tr class="odd">
            <td>BUSY</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 示忙坐席数量</td>
        </tr>
        <tr class="even">
            <td>TYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席类型</td>
        </tr>
        <tr class="odd">
            <td>DATESTR</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期字符串</td>
        </tr>
        <tr class="even">
            <td>HOURSTR</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 小时字符串</td>
        </tr>
        <tr class="odd">
            <td>DATEHOURSTR</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期小时字符串</td>
        </tr>
        <tr class="even">
            <td>worktype</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作类型</td>
        </tr>
        <tr class="odd">
            <td>workresult</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 操作结果</td>
        </tr>
        <tr class="even">
            <td>dataid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="微信用户信息">微信用户信息 </h2>
    <table>
        <caption>uk_weixinuser 微信用户信息</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>snsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="odd">
            <td>subscribe</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否关注公众号</td>
        </tr>
        <tr class="even">
            <td>openid</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> OPENID</td>
        </tr>
        <tr class="odd">
            <td>nickname</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 昵称</td>
        </tr>
        <tr class="even">
            <td>sex</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 性别</td>
        </tr>
        <tr class="odd">
            <td>language</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 语言</td>
        </tr>
        <tr class="even">
            <td>city</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="odd">
            <td>province</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="even">
            <td>country</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="odd">
            <td>headimgurl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 头像</td>
        </tr>
        <tr class="even">
            <td>subscribetime</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关注时间</td>
        </tr>
        <tr class="odd">
            <td>unionid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 唯一ID</td>
        </tr>
        <tr class="even">
            <td>sexid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 性别编码</td>
        </tr>
        <tr class="odd">
            <td>remark</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 签名</td>
        </tr>
        <tr class="even">
            <td>groupid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组ID</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>contactsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="坐席状态表-1">坐席状态表 </h2>
    <table>
        <caption>uk_work_monitor 坐席状态表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> ID</td>
        </tr>
        <tr class="even">
            <td>USERID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录人ID</td>
        </tr>
        <tr class="odd">
            <td>AGENT</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工号</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席用户名（登录名）</td>
        </tr>
        <tr class="odd">
            <td>AGENTNO</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机号（坐席登录的分机号码）</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席姓名</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席状态code（对应字典表里的CODE）</td>
        </tr>
        <tr class="even">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席当前状</td>
        </tr>
        <tr class="odd">
            <td>BUSY</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席是否忙</td>
        </tr>
        <tr class="even">
            <td>WORKSTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工作状态</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>AGENTSERVICEID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>SKILL</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组ID</td>
        </tr>
        <tr class="even">
            <td>SKILLNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组名称</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 记录创建时间</td>
        </tr>
        <tr class="even">
            <td>ANI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫号码</td>
        </tr>
        <tr class="odd">
            <td>CALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫号码</td>
        </tr>
        <tr class="even">
            <td>SOURCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="odd">
            <td>SERVICEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录ID</td>
        </tr>
        <tr class="even">
            <td>SERVICESTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前呼叫状态</td>
        </tr>
        <tr class="odd">
            <td>DISCALLER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫分机号</td>
        </tr>
        <tr class="even">
            <td>DISCALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫分机号</td>
        </tr>
        <tr class="odd">
            <td>ORGAN</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属组织机构ID</td>
        </tr>
        <tr class="even">
            <td>BEGINTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态开始时间</td>
        </tr>
        <tr class="odd">
            <td>ENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态结束时间</td>
        </tr>
        <tr class="even">
            <td>FIRSTSTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 当天首次时间</td>
        </tr>
        <tr class="odd">
            <td>DATESTR</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期字符串</td>
        </tr>
        <tr class="even">
            <td>DURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话时长</td>
        </tr>
        <tr class="odd">
            <td>EVENTID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件ID</td>
        </tr>
        <tr class="even">
            <td>WORKTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 业务类型</td>
        </tr>
        <tr class="odd">
            <td>CALLENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束呼叫事件</td>
        </tr>
        <tr class="even">
            <td>CALLSTARTTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始呼叫事件</td>
        </tr>
        <tr class="odd">
            <td>DIRECTION</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 呼叫方向</td>
        </tr>
        <tr class="even">
            <td>EXTNO</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机号码</td>
        </tr>
        <tr class="odd">
            <td>ADMIN</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否管理员</td>
        </tr>
        <tr class="even">
            <td>firsttime</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否首次就绪</td>
        </tr>
        <tr class="odd">
            <td>firsttimes</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 首次就绪时长</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="坐席状态表-2">坐席状态表 </h2>
    <table>
        <caption>uk_work_session 坐席状态表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> ID</td>
        </tr>
        <tr class="even">
            <td>USERID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 登录人ID</td>
        </tr>
        <tr class="odd">
            <td>AGENT</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工号</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席用户名（登录名）</td>
        </tr>
        <tr class="odd">
            <td>AGENTNO</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分机号（坐席登录的分机号码）</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席姓名</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席状态code（对应字典表里的CODE）</td>
        </tr>
        <tr class="even">
            <td>STATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席当前状</td>
        </tr>
        <tr class="odd">
            <td>BUSY</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 坐席是否忙</td>
        </tr>
        <tr class="even">
            <td>WORKSTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席工作状态</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>AGENTSERVICEID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>SKILL</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组ID</td>
        </tr>
        <tr class="even">
            <td>SKILLNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 接入的技能组名称</td>
        </tr>
        <tr class="odd">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 记录创建时间</td>
        </tr>
        <tr class="even">
            <td>ANI</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫号码</td>
        </tr>
        <tr class="odd">
            <td>CALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫号码</td>
        </tr>
        <tr class="even">
            <td>SOURCE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源</td>
        </tr>
        <tr class="odd">
            <td>SERVICEID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 服务记录ID</td>
        </tr>
        <tr class="even">
            <td>SERVICESTATUS</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 当前呼叫状态</td>
        </tr>
        <tr class="odd">
            <td>DISCALLER</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫分机号</td>
        </tr>
        <tr class="even">
            <td>DISCALLED</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 被叫分机号</td>
        </tr>
        <tr class="odd">
            <td>ORGAN</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 所属组织机构ID</td>
        </tr>
        <tr class="even">
            <td>BEGINTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态开始时间</td>
        </tr>
        <tr class="odd">
            <td>ENDTIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态结束时间</td>
        </tr>
        <tr class="even">
            <td>FIRSTSTATUS</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 当天首次时间</td>
        </tr>
        <tr class="odd">
            <td>DATESTR</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期字符串</td>
        </tr>
        <tr class="even">
            <td>DURATION</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话时长</td>
        </tr>
        <tr class="odd">
            <td>IPADDR</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话时长</td>
        </tr>
        <tr class="even">
            <td>HOSTNAME</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话时长</td>
        </tr>
        <tr class="odd">
            <td>ADMIN</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>firsttime</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 是否首次就绪</td>
        </tr>
        <tr class="odd">
            <td>firsttimes</td>
            <td>int(11)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 首次就绪时长</td>
        </tr>
        <tr class="even">
            <td>CLIENTID</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 客户端ID</td>
        </tr>
        <tr class="odd">
            <td>SESSIONID</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>WORKTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 业务类型</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="工单类型表">工单类型表 </h2>
    <table>
        <caption>uk_workorder_type 工单类型表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>BPM</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用流程</td>
        </tr>
        <tr class="even">
            <td>PROCESSID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="odd">
            <td>SLA</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 请SLA</td>
        </tr>
        <tr class="even">
            <td>SLAID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SLA指标ID</td>
        </tr>
        <tr class="odd">
            <td>PARENTID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上级分类ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="工单表">工单表 </h2>
    <table>
        <caption>uk_workorders 工单表</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> ORGI</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人用户名</td>
        </tr>
        <tr class="odd">
            <td>PARENT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PARENT</td>
        </tr>
        <tr class="even">
            <td>ORDERNO</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 工单编号</td>
        </tr>
        <tr class="odd">
            <td>SESSIONID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="even">
            <td>TITLE</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="odd">
            <td>CONTENT</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="even">
            <td>PRICE</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PRICE</td>
        </tr>
        <tr class="odd">
            <td>KEYWORD</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="even">
            <td>SUMMARY</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 摘要</td>
        </tr>
        <tr class="odd">
            <td>ANONYMOUS</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许匿名访问</td>
        </tr>
        <tr class="even">
            <td>TOP</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 置顶</td>
        </tr>
        <tr class="odd">
            <td>ESSENCE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 精华</td>
        </tr>
        <tr class="even">
            <td>ACCEPT</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 已采纳答案</td>
        </tr>
        <tr class="odd">
            <td>FINISH</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 已结束</td>
        </tr>
        <tr class="even">
            <td>ANSWERS</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复数量</td>
        </tr>
        <tr class="odd">
            <td>sviews</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 查看次数</td>
        </tr>
        <tr class="even">
            <td>FOLLOWERS</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关注数</td>
        </tr>
        <tr class="odd">
            <td>COLLECTIONS</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 收藏数</td>
        </tr>
        <tr class="even">
            <td>COMMENTS</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 评论数</td>
        </tr>
        <tr class="odd">
            <td>MOBILE</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 移动端</td>
        </tr>
        <tr class="even">
            <td>STATUS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>WOTYPE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 工单类型</td>
        </tr>
        <tr class="even">
            <td>DATASTATUS</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 数据状态</td>
        </tr>
        <tr class="odd">
            <td>CATE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>PRIORITY</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 优先级</td>
        </tr>
        <tr class="odd">
            <td>CONTACTS</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人</td>
        </tr>
        <tr class="even">
            <td>CUSID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 联系人ID</td>
        </tr>
        <tr class="odd">
            <td>INITIATOR</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 发起人</td>
        </tr>
        <tr class="even">
            <td>BPMID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 流程ID</td>
        </tr>
        <tr class="odd">
            <td>TAGS</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标签</td>
        </tr>
        <tr class="even">
            <td>ACCDEPT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 受理部门</td>
        </tr>
        <tr class="odd">
            <td>ACCUSER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 受理人</td>
        </tr>
        <tr class="even">
            <td>ASSIGNED</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 已受理</td>
        </tr>
        <tr class="odd">
            <td>ORGAN</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="even">
            <td>AGENT</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 坐席</td>
        </tr>
        <tr class="odd">
            <td>SHARES</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 共享</td>
        </tr>
        <tr class="even">
            <td>SKILL</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="odd">
            <td>ROWCOUNT</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 行数</td>
        </tr>
        <tr class="even">
            <td>KEY</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="odd">
            <td>MEMO</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="even">
            <td>frommobile</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="odd">
            <td>dataid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 业务数据ID</td>
        </tr>
        <tr class="even">
            <td>eventid</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 通话ID</td>
        </tr>
        <tr class="odd">
            <td>ani</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主叫号码</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="工作时间">工作时间 </h2>
    <table>
        <caption>uk_worktime 工作时间</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>hostid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> PBX主机ID</td>
        </tr>
        <tr class="even">
            <td>type</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="odd">
            <td>day</td>
            <td>varchar(0)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期</td>
        </tr>
        <tr class="even">
            <td>begintime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 开始时间</td>
        </tr>
        <tr class="odd">
            <td>endtime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 结束时间</td>
        </tr>
        <tr class="even">
            <td>timetype</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 时间类型</td>
        </tr>
        <tr class="odd">
            <td>wfrom</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 周开始</td>
        </tr>
        <tr class="even">
            <td>wto</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 周结束</td>
        </tr>
        <tr class="odd">
            <td>dfrom</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期开始</td>
        </tr>
        <tr class="even">
            <td>dto</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期结束</td>
        </tr>
        <tr class="odd">
            <td>wbegintime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 周开始时间</td>
        </tr>
        <tr class="even">
            <td>wendtime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 周结束时间</td>
        </tr>
        <tr class="odd">
            <td>dbegintime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期开始时间</td>
        </tr>
        <tr class="even">
            <td>dendtime</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 日期结束时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="微信事件记录">微信事件记录 </h2>
    <table>
        <caption>uk_wxmpevent 微信事件记录</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>fromuser</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 来源用户</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>country</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 国家</td>
        </tr>
        <tr class="odd">
            <td>city</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 城市</td>
        </tr>
        <tr class="even">
            <td>province</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 省份</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>event</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 事件</td>
        </tr>
        <tr class="even">
            <td>channel</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 渠道</td>
        </tr>
        <tr class="odd">
            <td>model</td>
            <td>varchar(10)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组件</td>
        </tr>
        <tr class="even">
            <td>appid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        <tr class="odd">
            <td>snsid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> SNSID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="机器人配置">机器人配置 </h2>
    <table>
        <caption>uk_xiaoe_config 机器人配置</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人用户名</td>
        </tr>
        <tr class="odd">
            <td>name</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>enableask</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许AI主动发起问答</td>
        </tr>
        <tr class="even">
            <td>askfirst</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AI优先</td>
        </tr>
        <tr class="odd">
            <td>enablescene</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用场景识别</td>
        </tr>
        <tr class="even">
            <td>scenefirst</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 优先命中场景</td>
        </tr>
        <tr class="odd">
            <td>enablekeyword</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用关键词命中</td>
        </tr>
        <tr class="even">
            <td>keywordnum</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词数量</td>
        </tr>
        <tr class="odd">
            <td>noresultmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 未命中回复消息</td>
        </tr>
        <tr class="even">
            <td>askqs</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 询问访客是否解决问题</td>
        </tr>
        <tr class="odd">
            <td>asktipmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 询问访客的文本</td>
        </tr>
        <tr class="even">
            <td>resolved</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 已解决的提示文本</td>
        </tr>
        <tr class="odd">
            <td>unresolved</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 未解决的提示文本</td>
        </tr>
        <tr class="even">
            <td>redirectagent</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 跳转到人工坐席</td>
        </tr>
        <tr class="odd">
            <td>redirecturl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 跳转到其他URL</td>
        </tr>
        <tr class="even">
            <td>asktimes</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 最长多久开始询问</td>
        </tr>
        <tr class="odd">
            <td>selectskill</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组</td>
        </tr>
        <tr class="even">
            <td>selectskillmsg</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 技能组消息</td>
        </tr>
        <tr class="odd">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人ID</td>
        </tr>
        <tr class="even">
            <td>welcomemsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人欢迎语</td>
        </tr>
        <tr class="odd">
            <td>waitmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人等待提示语</td>
        </tr>
        <tr class="even">
            <td>enableother</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用外部机器人</td>
        </tr>
        <tr class="odd">
            <td>otherfirst</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人优先</td>
        </tr>
        <tr class="even">
            <td>otherurl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人URL</td>
        </tr>
        <tr class="odd">
            <td>otherlogin</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人是否需要登录</td>
        </tr>
        <tr class="even">
            <td>otherappkey</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人APPKey</td>
        </tr>
        <tr class="odd">
            <td>otherappsec</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人APPSec</td>
        </tr>
        <tr class="even">
            <td>otherparam</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人参数</td>
        </tr>
        <tr class="odd">
            <td>othertempletinput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人提交参数模板</td>
        </tr>
        <tr class="even">
            <td>othertempletoutput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人回复参数解析模板</td>
        </tr>
        <tr class="odd">
            <td>othermethod</td>
            <td>varchar(20)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人提交方式</td>
        </tr>
        <tr class="even">
            <td>otherssl</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人启用SSL</td>
        </tr>
        <tr class="odd">
            <td>enablesuggest</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 启用推荐功能</td>
        </tr>
        <tr class="even">
            <td>suggestmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 推荐的提示信息</td>
        </tr>
        <tr class="odd">
            <td>oqrdetailurl</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人内容URL</td>
        </tr>
        <tr class="even">
            <td>oqrdetailinput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人详情输入参数</td>
        </tr>
        <tr class="odd">
            <td>oqrdetailoutput</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 外部机器人详情输出参数</td>
        </tr>
        <tr class="even">
            <td>othersuggestmsg</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 命中结果的推荐的提示信息</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="知识库分类">知识库分类 </h2>
    <table>
        <caption>uk_xiaoe_kbs_type 知识库分类</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>area</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域</td>
        </tr>
        <tr class="even">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 上级ID</td>
        </tr>
        <tr class="odd">
            <td>typeid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="机器人场景">机器人场景 </h2>
    <table>
        <caption>uk_xiaoe_scene 机器人场景</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>sessionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 标题</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="odd">
            <td>keyword</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="even">
            <td>summary</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 摘要</td>
        </tr>
        <tr class="odd">
            <td>anonymous</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 匿名访问</td>
        </tr>
        <tr class="even">
            <td>begintime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期开始时间</td>
        </tr>
        <tr class="odd">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期结束时间</td>
        </tr>
        <tr class="even">
            <td>top</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 置顶</td>
        </tr>
        <tr class="odd">
            <td>essence</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用场景</td>
        </tr>
        <tr class="even">
            <td>accept</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 启用</td>
        </tr>
        <tr class="odd">
            <td>finish</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否结束</td>
        </tr>
        <tr class="even">
            <td>answers</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答数量</td>
        </tr>
        <tr class="odd">
            <td>sviews</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>followers</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关注数量</td>
        </tr>
        <tr class="odd">
            <td>collections</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复数量</td>
        </tr>
        <tr class="even">
            <td>comments</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 评论数量</td>
        </tr>
        <tr class="odd">
            <td>mobile</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 移动端</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>tptype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>cate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>price</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 价格</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 部门</td>
        </tr>
        <tr class="odd">
            <td>replaytype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复类型</td>
        </tr>
        <tr class="even">
            <td>allowask</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许提问</td>
        </tr>
        <tr class="odd">
            <td>inputcon</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 输入条件</td>
        </tr>
        <tr class="even">
            <td>outputcon</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 输出条件</td>
        </tr>
        <tr class="odd">
            <td>userinput</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户输入</td>
        </tr>
        <tr class="even">
            <td>aireply</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> AI回复内容（首条）</td>
        </tr>
        <tr class="odd">
            <td>frommobile</td>
            <td>tinyint(4)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 移动端接入</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="机器人场景类型">机器人场景类型 </h2>
    <table>
        <caption>uk_xiaoe_scene_type 机器人场景类型</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>area</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 区域</td>
        </tr>
        <tr class="even">
            <td>parentid</td>
            <td>varchar(32)</td>
            <td> 0</td>
            <td> YES</td>
            <td></td>
            <td> 父级ID</td>
        </tr>
        <tr class="odd">
            <td>typeid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="机器人场景子项">机器人场景子项 </h2>
    <table>
        <caption>uk_xiaoe_sceneitem 机器人场景子项</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复内容</td>
        </tr>
        <tr class="odd">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="odd">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="even">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>sceneid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 场景ID</td>
        </tr>
        <tr class="even">
            <td>inx</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 序号</td>
        </tr>
        <tr class="odd">
            <td>itemtype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类型</td>
        </tr>
        <tr class="even">
            <td>replaytype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复类型</td>
        </tr>
        <tr class="odd">
            <td>allowask</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许主动提问</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="知识库-1">知识库 </h2>
    <table>
        <caption>uk_xiaoe_topic 知识库</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>sessionid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 会话ID</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 主题</td>
        </tr>
        <tr class="even">
            <td>content</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识库内容</td>
        </tr>
        <tr class="odd">
            <td>keyword</td>
            <td>varchar(100)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="even">
            <td>summary</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 摘要</td>
        </tr>
        <tr class="odd">
            <td>anonymous</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许匿名访问</td>
        </tr>
        <tr class="even">
            <td>begintime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期开始时间</td>
        </tr>
        <tr class="odd">
            <td>endtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 有效期结束时间</td>
        </tr>
        <tr class="even">
            <td>top</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 是否置顶</td>
        </tr>
        <tr class="odd">
            <td>essence</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 精华</td>
        </tr>
        <tr class="even">
            <td>accept</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 允许评论</td>
        </tr>
        <tr class="odd">
            <td>finish</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 已结束</td>
        </tr>
        <tr class="even">
            <td>answers</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回答数量</td>
        </tr>
        <tr class="odd">
            <td>sviews</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td></td>
        </tr>
        <tr class="even">
            <td>followers</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关注人数</td>
        </tr>
        <tr class="odd">
            <td>collections</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 引用次数</td>
        </tr>
        <tr class="even">
            <td>comments</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 回复数</td>
        </tr>
        <tr class="odd">
            <td>mobile</td>
            <td>tinyint(4)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 移动端支持</td>
        </tr>
        <tr class="even">
            <td>status</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 状态</td>
        </tr>
        <tr class="odd">
            <td>tptype</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类</td>
        </tr>
        <tr class="even">
            <td>cate</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类ID</td>
        </tr>
        <tr class="odd">
            <td>username</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>updatetime</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 修改时间</td>
        </tr>
        <tr class="even">
            <td>memo</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 备注</td>
        </tr>
        <tr class="odd">
            <td>price</td>
            <td>int(11)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 权重</td>
        </tr>
        <tr class="even">
            <td>organ</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 组织机构</td>
        </tr>
        <tr class="odd">
            <td>sms</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 短信模板</td>
        </tr>
        <tr class="even">
            <td>tts</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> TTS模板</td>
        </tr>
        <tr class="odd">
            <td>email</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 邮件模板</td>
        </tr>
        <tr class="even">
            <td>weixin</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 微信回复模板</td>
        </tr>
        <tr class="odd">
            <td>silimar</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 类似问题</td>
        </tr>
        <tr class="even">
            <td>aiid</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 机器人ID</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="知识库类似问题">知识库类似问题 </h2>
    <table>
        <caption>uk_xiaoe_topic_item 知识库类似问题</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>id</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> ID</td>
        </tr>
        <tr class="even">
            <td>topicid</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 知识id</td>
        </tr>
        <tr class="odd">
            <td>title</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 问题</td>
        </tr>
        <tr class="even">
            <td>orgi</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 产品id</td>
        </tr>
        <tr class="odd">
            <td>creater</td>
            <td>varchar(255)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>createtime</td>
            <td>timestamp</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="关键词">关键词 </h2>
    <table>
        <caption>uk_xiaoe_words 关键词</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>KEYWORD</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 关键词</td>
        </tr>
        <tr class="odd">
            <td>CONTENT</td>
            <td>text</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 内容</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        <tr class="odd">
            <td>SUPERORDINATE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 上位词</td>
        </tr>
        <tr class="even">
            <td>PARTOFSPEECH</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 词性</td>
        </tr>
        <tr class="odd">
            <td>CATE</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类</td>
        </tr>
        </tbody>
    </table>
    <p></p>
    <h2 id="词库类型">词库类型 </h2>
    <table>
        <caption>uk_xiaoe_words_type 词库类型</caption>
        <thead>
        <tr class="header">
            <th>字段名</th>
            <th>数据类型</th>
            <th>默认值</th>
            <th>允许非空</th>
            <th>自动递增</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td>ID</td>
            <td>varchar(32)</td>
            <td></td>
            <td> NO</td>
            <td></td>
            <td> 主键ID</td>
        </tr>
        <tr class="even">
            <td>NAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类名称</td>
        </tr>
        <tr class="odd">
            <td>CODE</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 分类代码</td>
        </tr>
        <tr class="even">
            <td>CREATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建时间</td>
        </tr>
        <tr class="odd">
            <td>CREATER</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 创建人ID</td>
        </tr>
        <tr class="even">
            <td>UPDATETIME</td>
            <td>datetime</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 更新时间</td>
        </tr>
        <tr class="odd">
            <td>ORGI</td>
            <td>varchar(32)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 租户ID</td>
        </tr>
        <tr class="even">
            <td>USERNAME</td>
            <td>varchar(50)</td>
            <td></td>
            <td> YES</td>
            <td></td>
            <td> 用户名</td>
        </tr>
        </tbody>
    </table>
</div>



## 附录2-项目结构
```
contact-center
│  pom.xml
├─src
│  ├─main
│  │  ├─java
│  │  │  ├─com
│  │  │  │  └─chatopera
│  │  │  │      └─cc
│  │  │  │          ├─aggregation
│  │  │  │          │  │  CallOutHangupAggsResult.java
│  │  │  │          │  │  CallOutHangupAuditResult.java
│  │  │  │          │  │  MathHelper.java
│  │  │  │          │  │  
│  │  │  │          │  └─filter
│  │  │  │          │          AgentCallOutFilter.java
│  │  │  │          │          AgentStatusBusyFilter.java
│  │  │  │          │          AgentStatusOrgiFilter.java
│  │  │  │          │          AgentUserOrgiFilter.java
│  │  │  │          │          AiCallOutFilter.java
│  │  │  │          │          
│  │  │  │          ├─app
│  │  │  │          │  │  com.chatopera.cc.app.Application.java
│  │  │  │          │  │  ServletInitializer.java
│  │  │  │          │  │  
│  │  │  │          │  ├─algorithm
│  │  │  │          │  │      AutomaticServiceDist.java
│  │  │  │          │  │      
│  │  │  │          │  ├─aspect
│  │  │  │          │  ├─basic
│  │  │  │          │  │  │  MainContext.java
│  │  │  │          │  │  │  MainUtils.java
│  │  │  │          │  │  │  Viewport.java
│  │  │  │          │  │  │  
│  │  │  │          │  │  ├─aop
│  │  │  │          │  │  │      SyncDatabaseExt.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  └─resource
│  │  │  │          │  │          ActivityResource.java
│  │  │  │          │  │          BatchResource.java
│  │  │  │          │  │          OutputTextFormat.java
│  │  │  │          │  │          Resource.java
│  │  │  │          │  │          
│  │  │  │          │  ├─cache
│  │  │  │          │  │  │  CacheBean.java
│  │  │  │          │  │  │  CacheHelper.java
│  │  │  │          │  │  │  CacheInstance.java
│  │  │  │          │  │  │  
│  │  │  │          │  │  └─hazelcast
│  │  │  │          │  │      │  HazlcastCacheHelper.java
│  │  │  │          │  │      │  
│  │  │  │          │  │      └─impl
│  │  │  │          │  │              AgentStatusCache.java
│  │  │  │          │  │              AgentUserCache.java
│  │  │  │          │  │              ApiUserCache.java
│  │  │  │          │  │              CallCenterCache.java
│  │  │  │          │  │              JobCache.java
│  │  │  │          │  │              MultiCache.java
│  │  │  │          │  │              OnlineCache.java
│  │  │  │          │  │              SystemCache.java
│  │  │  │          │  │              
│  │  │  │          │  ├─config
│  │  │  │          │  │      ApiConfigure.java
│  │  │  │          │  │      ApiRequestMatchingFilter.java
│  │  │  │          │  │      ApplicationStartupListener.java
│  │  │  │          │  │      DelegateRequestMatchingFilter.java
│  │  │  │          │  │      DisruptorConfigure.java
│  │  │  │          │  │      DruidConfiguration.java
│  │  │  │          │  │      ExecutorConfig.java
│  │  │  │          │  │      IMServerConfiguration.java
│  │  │  │          │  │      RedisConfigure.java
│  │  │  │          │  │      StartedEventListener.java
│  │  │  │          │  │      StringToDateConverter.java
│  │  │  │          │  │      UKeFuExceptionHandler.java
│  │  │  │          │  │      UKWebAppConfigurer.java
│  │  │  │          │  │      WebConfigBeans.java
│  │  │  │          │  │      WebSecurityConfig.java
│  │  │  │          │  │      WebServerConfiguration.java
│  │  │  │          │  │      
│  │  │  │          │  ├─handler
│  │  │  │          │  │  │  ApplicationController.java
│  │  │  │          │  │  │  Handler.java
│  │  │  │          │  │  │  LoginController.java
│  │  │  │          │  │  │  
│  │  │  │          │  │  ├─admin
│  │  │  │          │  │  │  │  AdminController.java
│  │  │  │          │  │  │  │  
│  │  │  │          │  │  │  ├─area
│  │  │  │          │  │  │  │      AreaController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─callcenter
│  │  │  │          │  │  │  │      CallCenterAclController.java
│  │  │  │          │  │  │  │      CallCenterBlackController.java
│  │  │  │          │  │  │  │      CallCenterController.java
│  │  │  │          │  │  │  │      CallCenterExtentionController.java
│  │  │  │          │  │  │  │      CallCenterIvrController.java
│  │  │  │          │  │  │  │      CallCenterMediaController.java
│  │  │  │          │  │  │  │      CallCenterResourceController.java
│  │  │  │          │  │  │  │      CallCenterRouterController.java
│  │  │  │          │  │  │  │      CallCenterSipTrunkController.java
│  │  │  │          │  │  │  │      CallCenterSkillController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─channel
│  │  │  │          │  │  │  │      CalloutChannelController.java
│  │  │  │          │  │  │  │      SNSAccountIMController.java
│  │  │  │          │  │  │  │      WebIMController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─config
│  │  │  │          │  │  │  │      SystemConfigController.java
│  │  │  │          │  │  │  │      SystemMessageController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─organ
│  │  │  │          │  │  │  │      OrganController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─pay
│  │  │  │          │  │  │  │      PayAccountController.java
│  │  │  │          │  │  │  │      PayMsgController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─role
│  │  │  │          │  │  │  │      RoleController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─skill
│  │  │  │          │  │  │  │      AgentSkillController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─system
│  │  │  │          │  │  │  │      HazelcastMonitorController.java
│  │  │  │          │  │  │  │      LogController.java
│  │  │  │          │  │  │  │      MetadataController.java
│  │  │  │          │  │  │  │      SysDicController.java
│  │  │  │          │  │  │  │      TemplateController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  └─users
│  │  │  │          │  │  │          UsersController.java
│  │  │  │          │  │  │          
│  │  │  │          │  │  ├─api
│  │  │  │          │  │  │  │  ApiLoginController.java
│  │  │  │          │  │  │  │  ApiTokensErrorController.java
│  │  │  │          │  │  │  │  
│  │  │  │          │  │  │  ├─request
│  │  │  │          │  │  │  │      QueryParams.java
│  │  │  │          │  │  │  │      RequestValues.java
│  │  │  │          │  │  │  │      RestUtils.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  └─rest
│  │  │  │          │  │  │          ApiAgentUserController.java
│  │  │  │          │  │  │          ApiCallMonitorController.java
│  │  │  │          │  │  │          ApiCallRecordsController.java
│  │  │  │          │  │  │          ApiChatbotController.java
│  │  │  │          │  │  │          ApiChatMessageController.java
│  │  │  │          │  │  │          ApiContactNotesController.java
│  │  │  │          │  │  │          ApiContactsController.java
│  │  │  │          │  │  │          ApiContactTagsController.java
│  │  │  │          │  │  │          ApiDailplanRunController.java
│  │  │  │          │  │  │          ApiLeavemsgController.java
│  │  │  │          │  │  │          ApiOnlineUserController.java
│  │  │  │          │  │  │          ApiOrganController.java
│  │  │  │          │  │  │          ApiQualityController.java
│  │  │  │          │  │  │          ApiQuickReplyController.java
│  │  │  │          │  │  │          ApiQuickTypeController.java
│  │  │  │          │  │  │          ApiServiceQueneController.java
│  │  │  │          │  │  │          ApiSysDicController.java
│  │  │  │          │  │  │          ApiTagsController.java
│  │  │  │          │  │  │          ApiUserController.java
│  │  │  │          │  │  │          ApiWebIMController.java
│  │  │  │          │  │  │          QueryParams.java
│  │  │  │          │  │  │          RequestValues.java
│  │  │  │          │  │  │          UkefuApiTagsController.java
│  │  │  │          │  │  │          
│  │  │  │          │  │  ├─apps
│  │  │  │          │  │  │  │  AppsController.java
│  │  │  │          │  │  │  │  
│  │  │  │          │  │  │  ├─agent
│  │  │  │          │  │  │  │      AgentController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─callcenter
│  │  │  │          │  │  │  │      ExtentionController.java
│  │  │  │          │  │  │  │      SipTrunkController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─callout
│  │  │  │          │  │  │  │      CalloutController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─chatbot
│  │  │  │          │  │  │  │      ChatbotController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─contacts
│  │  │  │          │  │  │  │      ContactsController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─customer
│  │  │  │          │  │  │  │      CustomerController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─entim
│  │  │  │          │  │  │  │      EntIMController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─internet
│  │  │  │          │  │  │  │      IMController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─job
│  │  │  │          │  │  │  │      JobController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─kbs
│  │  │  │          │  │  │  │      KbsController.java
│  │  │  │          │  │  │  │      TopicController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─message
│  │  │  │          │  │  │  │      MessageController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─organization
│  │  │  │          │  │  │  │      OrganizationController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─quality
│  │  │  │          │  │  │  │      AgentQualityController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─report
│  │  │  │          │  │  │  │      CubeController.java
│  │  │  │          │  │  │  │      CubeLevelController.java
│  │  │  │          │  │  │  │      CubeMeasureController.java
│  │  │  │          │  │  │  │      DimensionController.java
│  │  │  │          │  │  │  │      ReportController.java
│  │  │  │          │  │  │  │      ReportDesignController.java
│  │  │  │          │  │  │  │      ReportViewController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─service
│  │  │  │          │  │  │  │      AgentSummaryController.java
│  │  │  │          │  │  │  │      ChatServiceController.java
│  │  │  │          │  │  │  │      CommentController.java
│  │  │  │          │  │  │  │      OnlineUserController.java
│  │  │  │          │  │  │  │      ProcessedSummaryController.java
│  │  │  │          │  │  │  │      StatsController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─setting
│  │  │  │          │  │  │  │      IMAgentController.java
│  │  │  │          │  │  │  │      QuickReplyController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  ├─tenant
│  │  │  │          │  │  │  │      TenantController.java
│  │  │  │          │  │  │  │      
│  │  │  │          │  │  │  └─test
│  │  │  │          │  │  │          TestController.java
│  │  │  │          │  │  │          
│  │  │  │          │  │  └─resource
│  │  │  │          │  │          CallAgentResourceController.java
│  │  │  │          │  │          ContactsResourceController.java
│  │  │  │          │  │          CssResourceController.java
│  │  │  │          │  │          MediaController.java
│  │  │  │          │  │          QuickReplyResourceController.java
│  │  │  │          │  │          SysDicResourceController.java
│  │  │  │          │  │          UsersResourceController.java
│  │  │  │          │  │          
│  │  │  │          │  ├─im
│  │  │  │          │  │  │  ServerRunner.java
│  │  │  │          │  │  │  
│  │  │  │          │  │  ├─client
│  │  │  │          │  │  │      NettyAgentClient.java
│  │  │  │          │  │  │      NettyCallCenterClient.java
│  │  │  │          │  │  │      NettyCalloutClient.java
│  │  │  │          │  │  │      NettyChatbotClient.java
│  │  │  │          │  │  │      NettyClient.java
│  │  │  │          │  │  │      NettyClients.java
│  │  │  │          │  │  │      NettyIMClient.java
│  │  │  │          │  │  │      UserClient.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  ├─handler
│  │  │  │          │  │  │      AgentEventHandler.java
│  │  │  │          │  │  │      CalloutEventHandler.java
│  │  │  │          │  │  │      ChatbotEventHandler.java
│  │  │  │          │  │  │      EntIMEventHandler.java
│  │  │  │          │  │  │      IMEventHandler.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  ├─message
│  │  │  │          │  │  │      AgentServiceMessage.java
│  │  │  │          │  │  │      AgentStatusMessage.java
│  │  │  │          │  │  │      ChatMessage.java
│  │  │  │          │  │  │      ChatObject.java
│  │  │  │          │  │  │      Message.java
│  │  │  │          │  │  │      NewRequestMessage.java
│  │  │  │          │  │  │      OtherMessage.java
│  │  │  │          │  │  │      OtherMessageItem.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  ├─router
│  │  │  │          │  │  │      AgentUserRouter.java
│  │  │  │          │  │  │      CallOutMessageRouter.java
│  │  │  │          │  │  │      MessageRouter.java
│  │  │  │          │  │  │      OutMessageRouter.java
│  │  │  │          │  │  │      Router.java
│  │  │  │          │  │  │      RouterHelper.java
│  │  │  │          │  │  │      WebIMOutMessageRouter.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  └─util
│  │  │  │          │  │          ChatbotUtils.java
│  │  │  │          │  │          HumanUtils.java
│  │  │  │          │  │          IMServiceUtils.java
│  │  │  │          │  │          RichMediaUtils.java
│  │  │  │          │  │          
│  │  │  │          │  ├─interceptor
│  │  │  │          │  │      CrossInterceptorHandler.java
│  │  │  │          │  │      LogIntercreptorHandler.java
│  │  │  │          │  │      UserInterceptorHandler.java
│  │  │  │          │  │      
│  │  │  │          │  ├─model
│  │  │  │          │  │      Acl.java
│  │  │  │          │  │      AdType.java
│  │  │  │          │  │      AgentReport.java
│  │  │  │          │  │      AgentService.java
│  │  │  │          │  │      AgentServiceSatis.java
│  │  │  │          │  │      AgentServiceSummary.java
│  │  │  │          │  │      AgentStatus.java
│  │  │  │          │  │      AgentUser.java
│  │  │  │          │  │      AgentUserContacts.java
│  │  │  │          │  │      AgentUserTask.java
│  │  │  │          │  │      AiConfig.java
│  │  │  │          │  │      AiSNSAccount.java
│  │  │  │          │  │      AiUser.java
│  │  │  │          │  │      AreaType.java
│  │  │  │          │  │      AttachmentFile.java
│  │  │  │          │  │      BlackEntity.java
│  │  │  │          │  │      CallAgent.java
│  │  │  │          │  │      CallCenterSkill.java
│  │  │  │          │  │      CallMonitor.java
│  │  │  │          │  │      CallMonitorPerformance.java
│  │  │  │          │  │      CallOutDialplan.java
│  │  │  │          │  │      CallOutLogDialPlan.java
│  │  │  │          │  │      CallOutTarget.java
│  │  │  │          │  │      ChartProperties.java
│  │  │  │          │  │      Chatbot.java
│  │  │  │          │  │      ColumnProperties.java
│  │  │  │          │  │      ContactNotes.java
│  │  │  │          │  │      Contacts.java
│  │  │  │          │  │      CousultInvite.java
│  │  │  │          │  │      Cube.java
│  │  │  │          │  │      CubeLevel.java
│  │  │  │          │  │      CubeMeasure.java
│  │  │  │          │  │      CubeMetadata.java
│  │  │  │          │  │      CubeType.java
│  │  │  │          │  │      CustomerGroupForm.java
│  │  │  │          │  │      DataDic.java
│  │  │  │          │  │      DataEvent.java
│  │  │  │          │  │      Dimension.java
│  │  │  │          │  │      DrillDown.java
│  │  │  │          │  │      EntCustomer.java
│  │  │  │          │  │      ESBean.java
│  │  │  │          │  │      Extention.java
│  │  │  │          │  │      Favorites.java
│  │  │  │          │  │      FormFilter.java
│  │  │  │          │  │      FormFilterItem.java
│  │  │  │          │  │      FormFilterRequest.java
│  │  │  │          │  │      Generation.java
│  │  │  │          │  │      IMGroup.java
│  │  │  │          │  │      IMGroupUser.java
│  │  │  │          │  │      Instruction.java
│  │  │  │          │  │      InviteRecord.java
│  │  │  │          │  │      IvrMenu.java
│  │  │  │          │  │      JobDetail.java
│  │  │  │          │  │      JobTask.java
│  │  │  │          │  │      KbsExpert.java
│  │  │  │          │  │      KbsTopic.java
│  │  │  │          │  │      KbsTopicComment.java
│  │  │  │          │  │      KbsType.java
│  │  │  │          │  │      KnowledgeType.java
│  │  │  │          │  │      LeaveMsg.java
│  │  │  │          │  │      Log.java
│  │  │  │          │  │      Media.java
│  │  │  │          │  │      MessageDataBean.java
│  │  │  │          │  │      MessageInContent.java
│  │  │  │          │  │      MessageOutContent.java
│  │  │  │          │  │      MetadataTable.java
│  │  │  │          │  │      OnlineUser.java
│  │  │  │          │  │      OnlineUserHis.java
│  │  │  │          │  │      OrdersComment.java
│  │  │  │          │  │      Organ.java
│  │  │  │          │  │      Organization.java
│  │  │  │          │  │      OrganRole.java
│  │  │  │          │  │      OrgiSkillRel.java
│  │  │  │          │  │      PbxHost.java
│  │  │  │          │  │      ProcessContent.java
│  │  │  │          │  │      Product.java
│  │  │  │          │  │      PropertiesEvent.java
│  │  │  │          │  │      PublishedCube.java
│  │  │  │          │  │      PublishedReport.java
│  │  │  │          │  │      Quality.java
│  │  │  │          │  │      QualityRequest.java
│  │  │  │          │  │      QueSurveyAnswer.java
│  │  │  │          │  │      QueSurveyProcess.java
│  │  │  │          │  │      QueSurveyQuestion.java
│  │  │  │          │  │      QuickReply.java
│  │  │  │          │  │      QuickType.java
│  │  │  │          │  │      RecentUser.java
│  │  │  │          │  │      Report.java
│  │  │  │          │  │      Reporter.java
│  │  │  │          │  │      ReportFilter.java
│  │  │  │          │  │      ReportModel.java
│  │  │  │          │  │      RequestLog.java
│  │  │  │          │  │      Role.java
│  │  │  │          │  │      RoleAuth.java
│  │  │  │          │  │      RouterRules.java
│  │  │  │          │  │      SaleStatus.java
│  │  │  │          │  │      Scene.java
│  │  │  │          │  │      SceneType.java
│  │  │  │          │  │      Secret.java
│  │  │  │          │  │      ServiceAi.java
│  │  │  │          │  │      SessionConfig.java
│  │  │  │          │  │      SipTrunk.java
│  │  │  │          │  │      Skill.java
│  │  │  │          │  │      SkillExtention.java
│  │  │  │          │  │      SNSAccount.java
│  │  │  │          │  │      StatusEvent.java
│  │  │  │          │  │      StatusEventSatisf.java
│  │  │  │          │  │      StreamingFile.java
│  │  │  │          │  │      SysDic.java
│  │  │  │          │  │      SystemConfig.java
│  │  │  │          │  │      SystemMessage.java
│  │  │  │          │  │      TableProperties.java
│  │  │  │          │  │      Tag.java
│  │  │  │          │  │      TagRelation.java
│  │  │  │          │  │      Template.java
│  │  │  │          │  │      Tenant.java
│  │  │  │          │  │      Topic.java
│  │  │  │          │  │      TopicItem.java
│  │  │  │          │  │      UKAgg.java
│  │  │  │          │  │      UKefuCallOutConfig.java
│  │  │  │          │  │      UKefuCallOutFilter.java
│  │  │  │          │  │      UKefuCallOutNames.java
│  │  │  │          │  │      UKefuCallOutRole.java
│  │  │  │          │  │      UKefuCallOutTask.java
│  │  │  │          │  │      UKeFuDic.java
│  │  │  │          │  │      UKFacet.java
│  │  │  │          │  │      UkPayAccountEntity.java
│  │  │  │          │  │      UkPayMsgEntity.java
│  │  │  │          │  │      UploadStatus.java
│  │  │  │          │  │      User.java
│  │  │  │          │  │      UserHistory.java
│  │  │  │          │  │      UserRole.java
│  │  │  │          │  │      UserTraceHistory.java
│  │  │  │          │  │      WebIMReport.java
│  │  │  │          │  │      WeiXinUser.java
│  │  │  │          │  │      WorkMonitor.java
│  │  │  │          │  │      WorkOrders.java
│  │  │  │          │  │      WorkOrderType.java
│  │  │  │          │  │      WorkSession.java
│  │  │  │          │  │      WorkTime.java
│  │  │  │          │  │      WxMpEvent.java
│  │  │  │          │  │      
│  │  │  │          │  ├─persistence
│  │  │  │          │  │  ├─blob
│  │  │  │          │  │  │      JpaBlobHelper.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  ├─es
│  │  │  │          │  │  │      ContactNotesRepository.java
│  │  │  │          │  │  │      ContactsEsCommonRepository.java
│  │  │  │          │  │  │      ContactsRepository.java
│  │  │  │          │  │  │      ContactsRepositoryImpl.java
│  │  │  │          │  │  │      EntCustomerEsCommonRepository.java
│  │  │  │          │  │  │      EntCustomerRepository.java
│  │  │  │          │  │  │      EntCustomerRepositoryImpl.java
│  │  │  │          │  │  │      KbsTopicCommentEsCommonRepository.java
│  │  │  │          │  │  │      KbsTopicCommentRepository.java
│  │  │  │          │  │  │      KbsTopicCommentRepositoryImpl.java
│  │  │  │          │  │  │      KbsTopicEsCommonRepository.java
│  │  │  │          │  │  │      KbsTopicRepository.java
│  │  │  │          │  │  │      KbsTopicRepositoryImpl.java
│  │  │  │          │  │  │      QuickReplyEsCommonRepository.java
│  │  │  │          │  │  │      QuickReplyRepository.java
│  │  │  │          │  │  │      QuickReplyRepositoryImpl.java
│  │  │  │          │  │  │      TopicEsCommonRepository.java
│  │  │  │          │  │  │      TopicRepository.java
│  │  │  │          │  │  │      TopicRepositoryImpl.java
│  │  │  │          │  │  │      UKAggResultExtractor.java
│  │  │  │          │  │  │      UKAggTopResultExtractor.java
│  │  │  │          │  │  │      UKResultMapper.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  ├─hibernate
│  │  │  │          │  │  │      BaseService.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  ├─impl
│  │  │  │          │  │  │      AgentUserService.java
│  │  │  │          │  │  │      BatchDataProcess.java
│  │  │  │          │  │  │      CallOutQuene.java
│  │  │  │          │  │  │      ContactsDataExchangeImpl.java
│  │  │  │          │  │  │      DataBatProcess.java
│  │  │  │          │  │  │      ESDataExchangeImpl.java
│  │  │  │          │  │  │      HazelcastService.java
│  │  │  │          │  │  │      OrganDataExchangeImpl.java
│  │  │  │          │  │  │      QuickTypeDataExchangeImpl.java
│  │  │  │          │  │  │      ServiceDataExchangeImpl.java
│  │  │  │          │  │  │      TopicDataExchangeImpl.java
│  │  │  │          │  │  │      TopicMoreDataExchangeImpl.java
│  │  │  │          │  │  │      TopicTypeDataExchangeImpl.java
│  │  │  │          │  │  │      UserDataExchangeImpl.java
│  │  │  │          │  │  │      UserService.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  ├─repository
│  │  │  │          │  │  │      AclRepository.java
│  │  │  │          │  │  │      AdTypeRepository.java
│  │  │  │          │  │  │      AgentReportRepository.java
│  │  │  │          │  │  │      AgentServiceRepository.java
│  │  │  │          │  │  │      AgentServiceSatisRepository.java
│  │  │  │          │  │  │      AgentStatusRepository.java
│  │  │  │          │  │  │      AgentUserContactsRepository.java
│  │  │  │          │  │  │      AgentUserRepository.java
│  │  │  │          │  │  │      AgentUserTaskRepository.java
│  │  │  │          │  │  │      AiConfigRepository.java
│  │  │  │          │  │  │      AiSNSAccountRepository.java
│  │  │  │          │  │  │      AreaTypeRepository.java
│  │  │  │          │  │  │      AttachmentRepository.java
│  │  │  │          │  │  │      BaseRepository.java
│  │  │  │          │  │  │      BlackListRepository.java
│  │  │  │          │  │  │      CallAgentRepository.java
│  │  │  │          │  │  │      CallCenterSkillRepository.java
│  │  │  │          │  │  │      CallMonitorPerformanceRepository.java
│  │  │  │          │  │  │      CallMonitorRepository.java
│  │  │  │          │  │  │      CallOutDialplanRepository.java
│  │  │  │          │  │  │      CallOutLogDialPlanRepository.java
│  │  │  │          │  │  │      CallOutTargetRepository.java
│  │  │  │          │  │  │      ChatbotRepository.java
│  │  │  │          │  │  │      ChatMessageRepository.java
│  │  │  │          │  │  │      ColumnPropertiesRepository.java
│  │  │  │          │  │  │      ConsultInviteRepository.java
│  │  │  │          │  │  │      CubeLevelRepository.java
│  │  │  │          │  │  │      CubeMeasureRepository.java
│  │  │  │          │  │  │      CubeMetadataRepository.java
│  │  │  │          │  │  │      CubeRepository.java
│  │  │  │          │  │  │      CubeService.java
│  │  │  │          │  │  │      CubeTypeRepository.java
│  │  │  │          │  │  │      DataDicRepository.java
│  │  │  │          │  │  │      DataEventRepository.java
│  │  │  │          │  │  │      DataSourceService.java
│  │  │  │          │  │  │      DbDataRepository.java
│  │  │  │          │  │  │      DimensionRepository.java
│  │  │  │          │  │  │      DrilldownRepository.java
│  │  │  │          │  │  │      ExtentionRepository.java
│  │  │  │          │  │  │      FormFilterItemRepository.java
│  │  │  │          │  │  │      FormFilterRepository.java
│  │  │  │          │  │  │      GenerationRepository.java
│  │  │  │          │  │  │      IMGroupRepository.java
│  │  │  │          │  │  │      IMGroupUserRepository.java
│  │  │  │          │  │  │      InstructionRepository.java
│  │  │  │          │  │  │      InviteRecordRepository.java
│  │  │  │          │  │  │      IvrMenuRepository.java
│  │  │  │          │  │  │      JobDetailRepository.java
│  │  │  │          │  │  │      KbsExpertRepository.java
│  │  │  │          │  │  │      KbsTypeRepository.java
│  │  │  │          │  │  │      KnowledgeTypeRepository.java
│  │  │  │          │  │  │      LeaveMsgRepository.java
│  │  │  │          │  │  │      LogRepository.java
│  │  │  │          │  │  │      MediaRepository.java
│  │  │  │          │  │  │      MetadataRepository.java
│  │  │  │          │  │  │      OnlineUserHisRepository.java
│  │  │  │          │  │  │      OnlineUserRepository.java
│  │  │  │          │  │  │      OrganizationRepository.java
│  │  │  │          │  │  │      OrganRepository.java
│  │  │  │          │  │  │      OrganRoleRepository.java
│  │  │  │          │  │  │      OrgiSkillRelRepository.java
│  │  │  │          │  │  │      PbxHostRepository.java
│  │  │  │          │  │  │      ProcessContentRepository.java
│  │  │  │          │  │  │      ProductRepository.java
│  │  │  │          │  │  │      PropertiesEventRepository.java
│  │  │  │          │  │  │      PublishedCubeRepository.java
│  │  │  │          │  │  │      PublishedReportRepository.java
│  │  │  │          │  │  │      QualityRepository.java
│  │  │  │          │  │  │      QueSurveyAnswerRepository.java
│  │  │  │          │  │  │      QueSurveyProcessRepository.java
│  │  │  │          │  │  │      QueSurveyQuestionRepository.java
│  │  │  │          │  │  │      QuickTypeRepository.java
│  │  │  │          │  │  │      RecentUserRepository.java
│  │  │  │          │  │  │      ReportCubeService.java
│  │  │  │          │  │  │      ReporterRepository.java
│  │  │  │          │  │  │      ReportFilterRepository.java
│  │  │  │          │  │  │      ReportModelRepository.java
│  │  │  │          │  │  │      ReportRepository.java
│  │  │  │          │  │  │      RequestLogRepository.java
│  │  │  │          │  │  │      RoleAuthRepository.java
│  │  │  │          │  │  │      RoleRepository.java
│  │  │  │          │  │  │      RouterRulesRepository.java
│  │  │  │          │  │  │      SaleStatusRepository.java
│  │  │  │          │  │  │      SceneRepository.java
│  │  │  │          │  │  │      SecretRepository.java
│  │  │  │          │  │  │      ServiceAiRepository.java
│  │  │  │          │  │  │      ServiceSummaryRepository.java
│  │  │  │          │  │  │      SessionConfigRepository.java
│  │  │  │          │  │  │      SipTrunkRepository.java
│  │  │  │          │  │  │      SkillExtentionRepository.java
│  │  │  │          │  │  │      SkillRepository.java
│  │  │  │          │  │  │      SNSAccountRepository.java
│  │  │  │          │  │  │      StatusEventRepository.java
│  │  │  │          │  │  │      StatusEventSatisfRepository.java
│  │  │  │          │  │  │      StreamingFileRepository.java
│  │  │  │          │  │  │      SysDicRepository.java
│  │  │  │          │  │  │      SystemConfigRepository.java
│  │  │  │          │  │  │      SystemMessageRepository.java
│  │  │  │          │  │  │      TablePropertiesRepository.java
│  │  │  │          │  │  │      TagRelationRepository.java
│  │  │  │          │  │  │      TagRepository.java
│  │  │  │          │  │  │      TemplateRepository.java
│  │  │  │          │  │  │      TenantRepository.java
│  │  │  │          │  │  │      TopicItemRepository.java
│  │  │  │          │  │  │      UKefuCallOutConfigRepository.java
│  │  │  │          │  │  │      UKefuCallOutFilterRepository.java
│  │  │  │          │  │  │      UKefuCallOutNamesRepository.java
│  │  │  │          │  │  │      UKefuCallOutRoleRepository.java
│  │  │  │          │  │  │      UKefuCallOutTaskRepository.java
│  │  │  │          │  │  │      UkPayAccountRepository.java
│  │  │  │          │  │  │      UkPayMsgRepository.java
│  │  │  │          │  │  │      UserEventRepository.java
│  │  │  │          │  │  │      UserRepository.java
│  │  │  │          │  │  │      UserRoleRepository.java
│  │  │  │          │  │  │      UserTraceRepository.java
│  │  │  │          │  │  │      WeiXinUserRepository.java
│  │  │  │          │  │  │      WorkMonitorRepository.java
│  │  │  │          │  │  │      WorkOrderTypeRepository.java
│  │  │  │          │  │  │      WorkSessionRepository.java
│  │  │  │          │  │  │      WorkTimeRepository.java
│  │  │  │          │  │  │      WxMpEventRepository.java
│  │  │  │          │  │  │      XiaoEUKResultMapper.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  └─storage
│  │  │  │          │  │          MinioService.java
│  │  │  │          │  │          
│  │  │  │          │  └─schedule
│  │  │  │          │          CallOutPlanTask.java
│  │  │  │          │          CallOutSheetTask.java
│  │  │  │          │          CallOutWireTask.java
│  │  │  │          │          ExtTast.java
│  │  │  │          │          Fetcher.java
│  │  │  │          │          LogTask.java
│  │  │  │          │          NamesTask.java
│  │  │  │          │          Task.java
│  │  │  │          │          WebIMAgentDispatcher.java
│  │  │  │          │          WebIMOnlineUserDispatcher.java
│  │  │  │          │          WebIMTask.java
│  │  │  │          │          
│  │  │  │          ├─concurrent
│  │  │  │          │  ├─chatbot
│  │  │  │          │  │      ChatbotDisruptorExceptionHandler.java
│  │  │  │          │  │      ChatbotEvent.java
│  │  │  │          │  │      ChatbotEventFactory.java
│  │  │  │          │  │      ChatbotEventHandler.java
│  │  │  │          │  │      ChatbotEventProducer.java
│  │  │  │          │  │      
│  │  │  │          │  ├─dsdata
│  │  │  │          │  │  │  DataProcess.java
│  │  │  │          │  │  │  DSData.java
│  │  │  │          │  │  │  DSDataEvent.java
│  │  │  │          │  │  │  DSDataEventFactory.java
│  │  │  │          │  │  │  DSDataEventHandler.java
│  │  │  │          │  │  │  DSDataEventProducer.java
│  │  │  │          │  │  │  DSQueueProcessHandler.java
│  │  │  │          │  │  │  ESData.java
│  │  │  │          │  │  │  ExcelImportProecess.java
│  │  │  │          │  │  │  ExcelImportUtils.java
│  │  │  │          │  │  │  
│  │  │  │          │  │  ├─export
│  │  │  │          │  │  │      ExcelExporterProcess.java
│  │  │  │          │  │  │      ExportData.java
│  │  │  │          │  │  │      
│  │  │  │          │  │  └─process
│  │  │  │          │  │          ContactsProcess.java
│  │  │  │          │  │          EntCustomerProcess.java
│  │  │  │          │  │          JPAProcess.java
│  │  │  │          │  │          QuickReplyProcess.java
│  │  │  │          │  │          TopicProcess.java
│  │  │  │          │  │          
│  │  │  │          │  ├─multiupdate
│  │  │  │          │  │      MultiUpdateEvent.java
│  │  │  │          │  │      MultiUpdateEventFactory.java
│  │  │  │          │  │      MultiUpdateEventHandler.java
│  │  │  │          │  │      MultiUpdateEventProducer.java
│  │  │  │          │  │      
│  │  │  │          │  └─user
│  │  │  │          │          UserDataEvent.java
│  │  │  │          │          UserDataEventFactory.java
│  │  │  │          │          UserDataEventProducer.java
│  │  │  │          │          UserEventHandler.java
│  │  │  │          │          
│  │  │  │          ├─constant
│  │  │  │          │      CommonConstant.java
│  │  │  │          │      
│  │  │  │          ├─exception
│  │  │  │          │      CallOutRecordException.java
│  │  │  │          │      CallOutRuntimeException.java
│  │  │  │          │      CSKefuException.java
│  │  │  │          │      CSKefuRestException.java
│  │  │  │          │      FreeSwitchException.java
│  │  │  │          │      UCKeFuExceptionListener.java
│  │  │  │          │      
│  │  │  │          ├─exchange
│  │  │  │          │      CallCenterInterface.java
│  │  │  │          │      CallOutWireEvent.java
│  │  │  │          │      DataExchangeInterface.java
│  │  │  │          │      UserEvent.java
│  │  │  │          │      
│  │  │  │          └─util
│  │  │  │              │  AMRConvert.java
│  │  │  │              │  Base62.java
│  │  │  │              │  BrowserClient.java
│  │  │  │              │  CallCenterUtils.java
│  │  │  │              │  CallOutUtils.java
│  │  │  │              │  CheckMobile.java
│  │  │  │              │  CommonUtil.java
│  │  │  │              │  Constants.java
│  │  │  │              │  CronTools.java
│  │  │  │              │  DateConverter.java
│  │  │  │              │  ExtUtils.java
│  │  │  │              │  FFmpegCmdExecuter.java
│  │  │  │              │  HttpClientUtil.java
│  │  │  │              │  IP.java
│  │  │  │              │  IPTools.java
│  │  │  │              │  IPToolsBak.java
│  │  │  │              │  MD5.java
│  │  │  │              │  Menu.java
│  │  │  │              │  OnlineUserUtils.java
│  │  │  │              │  PinYinTools.java
│  │  │  │              │  PropertiesEventUtils.java
│  │  │  │              │  RestResult.java
│  │  │  │              │  RestResultType.java
│  │  │  │              │  StreamingFileUtils.java
│  │  │  │              │  SystemEnvHelper.java
│  │  │  │              │  TaskTools.java
│  │  │  │              │  Templet.java
│  │  │  │              │  TempletLoader.java
│  │  │  │              │  UCKeFuIdGenerator.java
│  │  │  │              │  UKeFuList.java
│  │  │  │              │  WebIMClient.java
│  │  │  │              │  WebIMReport.java
│  │  │  │              │  WebSseEmitterClient.java
│  │  │  │              │  WechatUtils.java
│  │  │  │              │  WeiXinReport.java
│  │  │  │              │  
│  │  │  │              ├─asr
│  │  │  │              │      AsrResult.java
│  │  │  │              │      
│  │  │  │              ├─bi
│  │  │  │              │  │  CubeReportData.java
│  │  │  │              │  │  ReportData.java
│  │  │  │              │  │  UKExcelUtil.java
│  │  │  │              │  │  
│  │  │  │              │  └─model
│  │  │  │              │          FirstTitle.java
│  │  │  │              │          Level.java
│  │  │  │              │          RequestData.java
│  │  │  │              │          ValueData.java
│  │  │  │              │          
│  │  │  │              ├─es
│  │  │  │              │      ESTools.java
│  │  │  │              │      SearchTools.java
│  │  │  │              │      UKDataBean.java
│  │  │  │              │      
│  │  │  │              ├─freeswitch
│  │  │  │              │  └─model
│  │  │  │              │          CallCenterAgent.java
│  │  │  │              │          
│  │  │  │              ├─jeecg
│  │  │  │              │      ApplicationContextUtil.java
│  │  │  │              │      BrowserType.java
│  │  │  │              │      BrowserUtils.java
│  │  │  │              │      ContextHolderUtils.java
│  │  │  │              │      DateUtils.java
│  │  │  │              │      DBTypeUtil.java
│  │  │  │              │      ExceptionUtil.java
│  │  │  │              │      FileUtils.java
│  │  │  │              │      GenericsUtils.java
│  │  │  │              │      ImportUtil.java
│  │  │  │              │      IpConfigMac.java
│  │  │  │              │      IpUtil.java
│  │  │  │              │      ListUtils.java
│  │  │  │              │      LogUtil.java
│  │  │  │              │      MD5Util.java
│  │  │  │              │      MyBeanUtils.java
│  │  │  │              │      MyClassLoader.java
│  │  │  │              │      oConvertUtils.java
│  │  │  │              │      ReflectHelper.java
│  │  │  │              │      SqlInjectionUtil.java
│  │  │  │              │      StringUtil.java
│  │  │  │              │      ToEntityUtil.java
│  │  │  │              │      UUIDGenerator.java
│  │  │  │              │      ZipUtil.java
│  │  │  │              │      
│  │  │  │              ├─json
│  │  │  │              │      GsonTools.java
│  │  │  │              │      
│  │  │  │              ├─log
│  │  │  │              │      UKeFuAppender.java
│  │  │  │              │      
│  │  │  │              ├─mail
│  │  │  │              │      Mail.java
│  │  │  │              │      MailAuthenticator.java
│  │  │  │              │      MailInfo.java
│  │  │  │              │      MailSender.java
│  │  │  │              │      
│  │  │  │              ├─metadata
│  │  │  │              │      DatabaseMetaDataHandler.java
│  │  │  │              │      UKColumnMetadata.java
│  │  │  │              │      UKDatabaseMetadata.java
│  │  │  │              │      UKTableMetaData.java
│  │  │  │              │      
│  │  │  │              └─mobile
│  │  │  │                      MobileAddress.java
│  │  │  │                      MobileNumberUtils.java
│  │  │  │                      
│  │  │  ├─mondrian
│  │  │  │  ├─calc
│  │  │  │  │  │  TupleList.java
│  │  │  │  │  │  
│  │  │  │  │  └─impl
│  │  │  │  │          ArrayTupleList.java
│  │  │  │  │          DelegatingTupleList.java
│  │  │  │  │          ListTupleList.java
│  │  │  │  │          UnaryTupleList.java
│  │  │  │  │          
│  │  │  │  ├─olap
│  │  │  │  │  │  Axis.java
│  │  │  │  │  │  Util.java
│  │  │  │  │  │  
│  │  │  │  │  └─fun
│  │  │  │  │          CountFunDef.java
│  │  │  │  │          FunUtil.java
│  │  │  │  │          SubsetFunDef.java
│  │  │  │  │          
│  │  │  │  ├─rolap
│  │  │  │  │  │  RolapAxis.java
│  │  │  │  │  │  RolapStar.java
│  │  │  │  │  │  SqlStatement.java
│  │  │  │  │  │  
│  │  │  │  │  └─agg
│  │  │  │  │          AbstractQuerySpec.java
│  │  │  │  │          DrillThroughQuerySpec.java
│  │  │  │  │          
│  │  │  │  └─spi
│  │  │  │      └─impl
│  │  │  │              SqlStatisticsProvider.java
│  │  │  │              
│  │  │  ├─net
│  │  │  │  └─coobird
│  │  │  │      └─thumbnailator
│  │  │  │          │  package-info.java
│  │  │  │          │  Thumbnailator.java
│  │  │  │          │  ThumbnailParameter.java
│  │  │  │          │  Thumbnails.java
│  │  │  │          │  
│  │  │  │          ├─builders
│  │  │  │          │      BufferedImageBuilder.java
│  │  │  │          │      package-info.java
│  │  │  │          │      ThumbnailParameterBuilder.java
│  │  │  │          │      
│  │  │  │          ├─filters
│  │  │  │          │      Canvas.java
│  │  │  │          │      Caption.java
│  │  │  │          │      Colorize.java
│  │  │  │          │      Flip.java
│  │  │  │          │      ImageFilter.java
│  │  │  │          │      package-info.java
│  │  │  │          │      Pipeline.java
│  │  │  │          │      Rotation.java
│  │  │  │          │      Transparency.java
│  │  │  │          │      Watermark.java
│  │  │  │          │      
│  │  │  │          ├─geometry
│  │  │  │          │      AbsoluteSize.java
│  │  │  │          │      Coordinate.java
│  │  │  │          │      package-info.java
│  │  │  │          │      Position.java
│  │  │  │          │      Positions.java
│  │  │  │          │      Region.java
│  │  │  │          │      RelativeSize.java
│  │  │  │          │      Size.java
│  │  │  │          │      
│  │  │  │          ├─makers
│  │  │  │          │      FixedSizeThumbnailMaker.java
│  │  │  │          │      package-info.java
│  │  │  │          │      ScaledThumbnailMaker.java
│  │  │  │          │      ThumbnailMaker.java
│  │  │  │          │      
│  │  │  │          ├─name
│  │  │  │          │      ConsecutivelyNumberedFilenames.java
│  │  │  │          │      package-info.java
│  │  │  │          │      Rename.java
│  │  │  │          │      
│  │  │  │          ├─resizers
│  │  │  │          │  │  AbstractResizer.java
│  │  │  │          │  │  BicubicResizer.java
│  │  │  │          │  │  BilinearResizer.java
│  │  │  │          │  │  DefaultResizerFactory.java
│  │  │  │          │  │  FixedResizerFactory.java
│  │  │  │          │  │  NullResizer.java
│  │  │  │          │  │  package-info.java
│  │  │  │          │  │  ProgressiveBilinearResizer.java
│  │  │  │          │  │  Resizer.java
│  │  │  │          │  │  ResizerFactory.java
│  │  │  │          │  │  Resizers.java
│  │  │  │          │  │  
│  │  │  │          │  └─configurations
│  │  │  │          │          AlphaInterpolation.java
│  │  │  │          │          Antialiasing.java
│  │  │  │          │          Dithering.java
│  │  │  │          │          package-info.java
│  │  │  │          │          Rendering.java
│  │  │  │          │          ResizerConfiguration.java
│  │  │  │          │          ScalingMode.java
│  │  │  │          │          
│  │  │  │          ├─tasks
│  │  │  │          │  │  FileThumbnailTask.java
│  │  │  │          │  │  package-info.java
│  │  │  │          │  │  SourceSinkThumbnailTask.java
│  │  │  │          │  │  StreamThumbnailTask.java
│  │  │  │          │  │  ThumbnailTask.java
│  │  │  │          │  │  UnsupportedFormatException.java
│  │  │  │          │  │  
│  │  │  │          │  └─io
│  │  │  │          │          AbstractImageSink.java
│  │  │  │          │          AbstractImageSource.java
│  │  │  │          │          BufferedImageSink.java
│  │  │  │          │          BufferedImageSource.java
│  │  │  │          │          FileImageSink.java
│  │  │  │          │          FileImageSource.java
│  │  │  │          │          ImageSink.java
│  │  │  │          │          ImageSource.java
│  │  │  │          │          InputStreamImageSource.java
│  │  │  │          │          OutputStreamImageSink.java
│  │  │  │          │          package-info.java
│  │  │  │          │          URLImageSource.java
│  │  │  │          │          
│  │  │  │          └─util
│  │  │  │              │  BufferedImages.java
│  │  │  │              │  package-info.java
│  │  │  │              │  ThumbnailatorUtils.java
│  │  │  │              │  
│  │  │  │              └─exif
│  │  │  │                      ExifFilterUtils.java
│  │  │  │                      ExifUtils.java
│  │  │  │                      IfdStructure.java
│  │  │  │                      IfdType.java
│  │  │  │                      Orientation.java
│  │  │  │                      package-info.java
│  │  │  │                      
│  │  │  └─org
│  │  │      └─eigenbase
│  │  │          └─resgen
│  │  │                  ShadowResourceBundle.java
│  │  │                  
│  │  ├─resources
│  │  │  │  application.properties
│  │  │  │  banner.txt
│  │  │  │  jcseg.properties
│  │  │  │  logback-spring.xml
│  │  │  │  logback-spring2.xml
│  │  │  │  messages.properties
│  │  │  │  
│  │  │  ├─config
│  │  │  │      hazelcast.xml
│  │  │  │      
│  │  │  ├─static
│  │  │  │  │  error.html
│  │  │  │  │  layui.js
│  │  │  │  │  testclient.html
│  │  │  │  │  
│  │  │  │  ├─css
│  │  │  │  │  │  audioplayer.css
│  │  │  │  │  │  cskefu-callout.css
│  │  │  │  │  │  darktooltip.css
│  │  │  │  │  │  entim.css
│  │  │  │  │  │  flexboxgrid.min.css
│  │  │  │  │  │  iconfont.eot
│  │  │  │  │  │  jquery.orgchart.css
│  │  │  │  │  │  kindeditor-suggest.css
│  │  │  │  │  │  layui.css
│  │  │  │  │  │  layui.mobile.css
│  │  │  │  │  │  login.css
│  │  │  │  │  │  modal-bg.png
│  │  │  │  │  │  snaker.css
│  │  │  │  │  │  ukefu-design.css
│  │  │  │  │  │  ukefu-view.css
│  │  │  │  │  │  
│  │  │  │  │  └─modules
│  │  │  │  │      │  code.css
│  │  │  │  │      │  
│  │  │  │  │      ├─laydate
│  │  │  │  │      │      icon.png
│  │  │  │  │      │      laydate.css
│  │  │  │  │      │      
│  │  │  │  │      └─layer
│  │  │  │  │          └─default
│  │  │  │  │                  icon-ext.png
│  │  │  │  │                  icon.png
│  │  │  │  │                  layer.css
│  │  │  │  │                  loading-0.gif
│  │  │  │  │                  loading-1.gif
│  │  │  │  │                  loading-2.gif
│  │  │  │  │                  
│  │  │  │  ├─font
│  │  │  │  │      iconfont.eot
│  │  │  │  │      iconfont.svg
│  │  │  │  │      iconfont.ttf
│  │  │  │  │      iconfont.woff
│  │  │  │  │      iconfont.woff2
│  │  │  │  │      
│  │  │  │  ├─font.bak
│  │  │  │  │      iconfont.svg
│  │  │  │  │      iconfont.ttf
│  │  │  │  │      iconfont.woff
│  │  │  │  │      
│  │  │  │  ├─im
│  │  │  │  │  ├─css
│  │  │  │  │  │  │  layui.css
│  │  │  │  │  │  │  ukefu.css
│  │  │  │  │  │  │  
│  │  │  │  │  │  ├─blue01
│  │  │  │  │  │  │      R3.css
│  │  │  │  │  │  │      
│  │  │  │  │  │  ├─blue02
│  │  │  │  │  │  │      R3.css
│  │  │  │  │  │  │      
│  │  │  │  │  │  ├─default
│  │  │  │  │  │  │      ukefu.css
│  │  │  │  │  │  │      
│  │  │  │  │  │  ├─gray
│  │  │  │  │  │  │      R3.css
│  │  │  │  │  │  │      
│  │  │  │  │  │  ├─purple
│  │  │  │  │  │  │      R3.css
│  │  │  │  │  │  │      
│  │  │  │  │  │  └─red
│  │  │  │  │  │          R3.css
│  │  │  │  │  │          
│  │  │  │  │  ├─img
│  │  │  │  │  │      ask.png
│  │  │  │  │  │      close.jpg
│  │  │  │  │  │      cut.png
│  │  │  │  │  │      e.png
│  │  │  │  │  │      face.png
│  │  │  │  │  │      file.png
│  │  │  │  │  │      img.png
│  │  │  │  │  │      logo-icon.png
│  │  │  │  │  │      logo.png
│  │  │  │  │  │      pic01.jpg
│  │  │  │  │  │      pic02.jpg
│  │  │  │  │  │      plus.png
│  │  │  │  │  │      r3-arrowL.png
│  │  │  │  │  │      r3-arrowR.png
│  │  │  │  │  │      r3-arrowT.png
│  │  │  │  │  │      r3-phone.png
│  │  │  │  │  │      r3-phone0.png
│  │  │  │  │  │      r3-times.png
│  │  │  │  │  │      r3-times0.png
│  │  │  │  │  │      r3-tv.png
│  │  │  │  │  │      r3-tv_03.png
│  │  │  │  │  │      send.png
│  │  │  │  │  │      server.png
│  │  │  │  │  │      sprite.png
│  │  │  │  │  │      user.png
│  │  │  │  │  │      voice.png
│  │  │  │  │  │      webwxgetmsgimg.jpg
│  │  │  │  │  │      xiaoe.png
│  │  │  │  │  │      
│  │  │  │  │  └─js
│  │  │  │  │      │  socket.io.js
│  │  │  │  │      │  
│  │  │  │  │      └─kindeditor
│  │  │  │  │          │  kindeditor-all-min.js
│  │  │  │  │          │  kindeditor-all.js
│  │  │  │  │          │  kindeditor-min.js
│  │  │  │  │          │  kindeditor.js
│  │  │  │  │          │  license.txt
│  │  │  │  │          │  
│  │  │  │  │          ├─lang
│  │  │  │  │          │      ar.js
│  │  │  │  │          │      en.js
│  │  │  │  │          │      ko.js
│  │  │  │  │          │      ru.js
│  │  │  │  │          │      zh-CN.js
│  │  │  │  │          │      zh-TW.js
│  │  │  │  │          │      
│  │  │  │  │          ├─plugins
│  │  │  │  │          │  ├─anchor
│  │  │  │  │          │  │      anchor.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─autoheight
│  │  │  │  │          │  │      autoheight.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─baidumap
│  │  │  │  │          │  │      baidumap.js
│  │  │  │  │          │  │      index.html
│  │  │  │  │          │  │      map.html
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─clearhtml
│  │  │  │  │          │  │      clearhtml.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─code
│  │  │  │  │          │  │      code.js
│  │  │  │  │          │  │      prettify.css
│  │  │  │  │          │  │      prettify.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─emoticons
│  │  │  │  │          │  │  │  emoticons.js
│  │  │  │  │          │  │  │  
│  │  │  │  │          │  │  └─images
│  │  │  │  │          │  │          0.gif
│  │  │  │  │          │  │          0.png
│  │  │  │  │          │  │          1.gif
│  │  │  │  │          │  │          1.png
│  │  │  │  │          │  │          10.gif
│  │  │  │  │          │  │          10.png
│  │  │  │  │          │  │          100.gif
│  │  │  │  │          │  │          100.png
│  │  │  │  │          │  │          101.gif
│  │  │  │  │          │  │          101.png
│  │  │  │  │          │  │          102.gif
│  │  │  │  │          │  │          102.png
│  │  │  │  │          │  │          103.gif
│  │  │  │  │          │  │          103.png
│  │  │  │  │          │  │          104.gif
│  │  │  │  │          │  │          104.png
│  │  │  │  │          │  │          11.gif
│  │  │  │  │          │  │          11.png
│  │  │  │  │          │  │          12.gif
│  │  │  │  │          │  │          12.png
│  │  │  │  │          │  │          13.gif
│  │  │  │  │          │  │          13.png
│  │  │  │  │          │  │          14.gif
│  │  │  │  │          │  │          14.png
│  │  │  │  │          │  │          15.gif
│  │  │  │  │          │  │          15.png
│  │  │  │  │          │  │          16.gif
│  │  │  │  │          │  │          16.png
│  │  │  │  │          │  │          17.gif
│  │  │  │  │          │  │          17.png
│  │  │  │  │          │  │          18.gif
│  │  │  │  │          │  │          18.png
│  │  │  │  │          │  │          19.gif
│  │  │  │  │          │  │          19.png
│  │  │  │  │          │  │          2.gif
│  │  │  │  │          │  │          2.png
│  │  │  │  │          │  │          20.gif
│  │  │  │  │          │  │          20.png
│  │  │  │  │          │  │          21.gif
│  │  │  │  │          │  │          21.png
│  │  │  │  │          │  │          22.gif
│  │  │  │  │          │  │          22.png
│  │  │  │  │          │  │          23.gif
│  │  │  │  │          │  │          23.png
│  │  │  │  │          │  │          24.gif
│  │  │  │  │          │  │          24.png
│  │  │  │  │          │  │          25.gif
│  │  │  │  │          │  │          25.png
│  │  │  │  │          │  │          26.gif
│  │  │  │  │          │  │          26.png
│  │  │  │  │          │  │          27.gif
│  │  │  │  │          │  │          27.png
│  │  │  │  │          │  │          28.gif
│  │  │  │  │          │  │          28.png
│  │  │  │  │          │  │          29.gif
│  │  │  │  │          │  │          29.png
│  │  │  │  │          │  │          3.gif
│  │  │  │  │          │  │          3.png
│  │  │  │  │          │  │          30.gif
│  │  │  │  │          │  │          30.png
│  │  │  │  │          │  │          31.gif
│  │  │  │  │          │  │          31.png
│  │  │  │  │          │  │          32.gif
│  │  │  │  │          │  │          32.png
│  │  │  │  │          │  │          33.gif
│  │  │  │  │          │  │          33.png
│  │  │  │  │          │  │          34.gif
│  │  │  │  │          │  │          34.png
│  │  │  │  │          │  │          35.gif
│  │  │  │  │          │  │          35.png
│  │  │  │  │          │  │          36.gif
│  │  │  │  │          │  │          36.png
│  │  │  │  │          │  │          37.gif
│  │  │  │  │          │  │          37.png
│  │  │  │  │          │  │          38.gif
│  │  │  │  │          │  │          38.png
│  │  │  │  │          │  │          39.gif
│  │  │  │  │          │  │          39.png
│  │  │  │  │          │  │          4.gif
│  │  │  │  │          │  │          4.png
│  │  │  │  │          │  │          40.gif
│  │  │  │  │          │  │          40.png
│  │  │  │  │          │  │          41.gif
│  │  │  │  │          │  │          41.png
│  │  │  │  │          │  │          42.gif
│  │  │  │  │          │  │          42.png
│  │  │  │  │          │  │          43.gif
│  │  │  │  │          │  │          43.png
│  │  │  │  │          │  │          44.gif
│  │  │  │  │          │  │          44.png
│  │  │  │  │          │  │          45.gif
│  │  │  │  │          │  │          45.png
│  │  │  │  │          │  │          46.gif
│  │  │  │  │          │  │          46.png
│  │  │  │  │          │  │          47.gif
│  │  │  │  │          │  │          47.png
│  │  │  │  │          │  │          48.gif
│  │  │  │  │          │  │          48.png
│  │  │  │  │          │  │          49.gif
│  │  │  │  │          │  │          49.png
│  │  │  │  │          │  │          5.gif
│  │  │  │  │          │  │          5.png
│  │  │  │  │          │  │          50.gif
│  │  │  │  │          │  │          50.png
│  │  │  │  │          │  │          51.gif
│  │  │  │  │          │  │          51.png
│  │  │  │  │          │  │          52.gif
│  │  │  │  │          │  │          52.png
│  │  │  │  │          │  │          53.gif
│  │  │  │  │          │  │          53.png
│  │  │  │  │          │  │          54.gif
│  │  │  │  │          │  │          54.png
│  │  │  │  │          │  │          55.gif
│  │  │  │  │          │  │          55.png
│  │  │  │  │          │  │          56.gif
│  │  │  │  │          │  │          56.png
│  │  │  │  │          │  │          57.gif
│  │  │  │  │          │  │          57.png
│  │  │  │  │          │  │          58.gif
│  │  │  │  │          │  │          58.png
│  │  │  │  │          │  │          59.gif
│  │  │  │  │          │  │          59.png
│  │  │  │  │          │  │          6.gif
│  │  │  │  │          │  │          6.png
│  │  │  │  │          │  │          60.gif
│  │  │  │  │          │  │          60.png
│  │  │  │  │          │  │          61.gif
│  │  │  │  │          │  │          61.png
│  │  │  │  │          │  │          62.gif
│  │  │  │  │          │  │          62.png
│  │  │  │  │          │  │          63.gif
│  │  │  │  │          │  │          63.png
│  │  │  │  │          │  │          64.gif
│  │  │  │  │          │  │          64.png
│  │  │  │  │          │  │          65.gif
│  │  │  │  │          │  │          65.png
│  │  │  │  │          │  │          66.gif
│  │  │  │  │          │  │          66.png
│  │  │  │  │          │  │          67.gif
│  │  │  │  │          │  │          67.png
│  │  │  │  │          │  │          68.gif
│  │  │  │  │          │  │          68.png
│  │  │  │  │          │  │          69.gif
│  │  │  │  │          │  │          69.png
│  │  │  │  │          │  │          7.gif
│  │  │  │  │          │  │          7.png
│  │  │  │  │          │  │          70.gif
│  │  │  │  │          │  │          70.png
│  │  │  │  │          │  │          71.gif
│  │  │  │  │          │  │          71.png
│  │  │  │  │          │  │          72.gif
│  │  │  │  │          │  │          72.png
│  │  │  │  │          │  │          73.gif
│  │  │  │  │          │  │          73.png
│  │  │  │  │          │  │          74.gif
│  │  │  │  │          │  │          74.png
│  │  │  │  │          │  │          75.gif
│  │  │  │  │          │  │          75.png
│  │  │  │  │          │  │          76.gif
│  │  │  │  │          │  │          76.png
│  │  │  │  │          │  │          77.gif
│  │  │  │  │          │  │          77.png
│  │  │  │  │          │  │          78.gif
│  │  │  │  │          │  │          78.png
│  │  │  │  │          │  │          79.gif
│  │  │  │  │          │  │          79.png
│  │  │  │  │          │  │          8.gif
│  │  │  │  │          │  │          8.png
│  │  │  │  │          │  │          80.gif
│  │  │  │  │          │  │          80.png
│  │  │  │  │          │  │          81.gif
│  │  │  │  │          │  │          81.png
│  │  │  │  │          │  │          82.gif
│  │  │  │  │          │  │          82.png
│  │  │  │  │          │  │          83.gif
│  │  │  │  │          │  │          83.png
│  │  │  │  │          │  │          84.gif
│  │  │  │  │          │  │          84.png
│  │  │  │  │          │  │          85.gif
│  │  │  │  │          │  │          85.png
│  │  │  │  │          │  │          86.gif
│  │  │  │  │          │  │          86.png
│  │  │  │  │          │  │          87.gif
│  │  │  │  │          │  │          87.png
│  │  │  │  │          │  │          88.gif
│  │  │  │  │          │  │          88.png
│  │  │  │  │          │  │          89.gif
│  │  │  │  │          │  │          89.png
│  │  │  │  │          │  │          9.gif
│  │  │  │  │          │  │          9.png
│  │  │  │  │          │  │          90.gif
│  │  │  │  │          │  │          90.png
│  │  │  │  │          │  │          91.gif
│  │  │  │  │          │  │          91.png
│  │  │  │  │          │  │          92.gif
│  │  │  │  │          │  │          92.png
│  │  │  │  │          │  │          93.gif
│  │  │  │  │          │  │          93.png
│  │  │  │  │          │  │          94.gif
│  │  │  │  │          │  │          94.png
│  │  │  │  │          │  │          95.gif
│  │  │  │  │          │  │          95.png
│  │  │  │  │          │  │          96.gif
│  │  │  │  │          │  │          96.png
│  │  │  │  │          │  │          97.gif
│  │  │  │  │          │  │          97.png
│  │  │  │  │          │  │          98.gif
│  │  │  │  │          │  │          98.png
│  │  │  │  │          │  │          99.gif
│  │  │  │  │          │  │          99.png
│  │  │  │  │          │  │          
│  │  │  │  │          │  ├─filemanager
│  │  │  │  │          │  │  │  filemanager.js
│  │  │  │  │          │  │  │  
│  │  │  │  │          │  │  └─images
│  │  │  │  │          │  │          file-16.gif
│  │  │  │  │          │  │          file-64.gif
│  │  │  │  │          │  │          folder-16.gif
│  │  │  │  │          │  │          folder-64.gif
│  │  │  │  │          │  │          go-up.gif
│  │  │  │  │          │  │          
│  │  │  │  │          │  ├─fixtoolbar
│  │  │  │  │          │  │      fixtoolbar.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─flash
│  │  │  │  │          │  │      flash.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─image
│  │  │  │  │          │  │  │  image.js
│  │  │  │  │          │  │  │  
│  │  │  │  │          │  │  └─images
│  │  │  │  │          │  │          align_left.gif
│  │  │  │  │          │  │          align_right.gif
│  │  │  │  │          │  │          align_top.gif
│  │  │  │  │          │  │          refresh.png
│  │  │  │  │          │  │          
│  │  │  │  │          │  ├─insertfile
│  │  │  │  │          │  │      insertfile.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─lineheight
│  │  │  │  │          │  │      lineheight.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─link
│  │  │  │  │          │  │      link.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─map
│  │  │  │  │          │  │      map.html
│  │  │  │  │          │  │      map.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─media
│  │  │  │  │          │  │      media.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─multiimage
│  │  │  │  │          │  │  │  multiimage.js
│  │  │  │  │          │  │  │  
│  │  │  │  │          │  │  └─images
│  │  │  │  │          │  │          image.png
│  │  │  │  │          │  │          select-files-en.png
│  │  │  │  │          │  │          select-files-zh-CN.png
│  │  │  │  │          │  │          swfupload.swf
│  │  │  │  │          │  │          
│  │  │  │  │          │  ├─pagebreak
│  │  │  │  │          │  │      pagebreak.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─plainpaste
│  │  │  │  │          │  │      plainpaste.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─preview
│  │  │  │  │          │  │      preview.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─quickformat
│  │  │  │  │          │  │      quickformat.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─table
│  │  │  │  │          │  │      table.js
│  │  │  │  │          │  │      
│  │  │  │  │          │  ├─template
│  │  │  │  │          │  │  │  template.js
│  │  │  │  │          │  │  │  
│  │  │  │  │          │  │  └─html
│  │  │  │  │          │  │          1.html
│  │  │  │  │          │  │          2.html
│  │  │  │  │          │  │          3.html
│  │  │  │  │          │  │          
│  │  │  │  │          │  └─wordpaste
│  │  │  │  │          │          wordpaste.js
│  │  │  │  │          │          
│  │  │  │  │          └─themes
│  │  │  │  │              ├─common
│  │  │  │  │              │      anchor.gif
│  │  │  │  │              │      blank.gif
│  │  │  │  │              │      flash.gif
│  │  │  │  │              │      loading.gif
│  │  │  │  │              │      media.gif
│  │  │  │  │              │      rm.gif
│  │  │  │  │              │      
│  │  │  │  │              ├─default
│  │  │  │  │              │      background.png
│  │  │  │  │              │      default.css
│  │  │  │  │              │      default.png
│  │  │  │  │              │      
│  │  │  │  │              ├─qq
│  │  │  │  │              │      editor.gif
│  │  │  │  │              │      qq.css
│  │  │  │  │              │      
│  │  │  │  │              └─simple
│  │  │  │  │                      simple.css
│  │  │  │  │                      
│  │  │  │  ├─images
│  │  │  │  │  │  admin.png
│  │  │  │  │  │  agent.png
│  │  │  │  │  │  area.png
│  │  │  │  │  │  banner.jpg
│  │  │  │  │  │  bg.jpg
│  │  │  │  │  │  cde-ico-gray.png
│  │  │  │  │  │  circle.png
│  │  │  │  │  │  creater.png
│  │  │  │  │  │  dept.png
│  │  │  │  │  │  dir.png
│  │  │  │  │  │  empty.png
│  │  │  │  │  │  error.jpg
│  │  │  │  │  │  favicon.ico
│  │  │  │  │  │  file.png
│  │  │  │  │  │  handle.png
│  │  │  │  │  │  iconloop.png
│  │  │  │  │  │  icon_chuangjian.png
│  │  │  │  │  │  icon_eye_closed.png
│  │  │  │  │  │  icon_eye_open.png
│  │  │  │  │  │  imgroup.png
│  │  │  │  │  │  imr.png
│  │  │  │  │  │  login_bj.png
│  │  │  │  │  │  logo-icon.png
│  │  │  │  │  │  logo-old.png
│  │  │  │  │  │  logo.png
│  │  │  │  │  │  logo2-old.png
│  │  │  │  │  │  logo2.png
│  │  │  │  │  │  menu.png
│  │  │  │  │  │  message.mp3
│  │  │  │  │  │  new.mp3
│  │  │  │  │  │  phone-ico.png
│  │  │  │  │  │  quickreply.png
│  │  │  │  │  │  ring.mp3
│  │  │  │  │  │  setting.png
│  │  │  │  │  │  title.jpg
│  │  │  │  │  │  titlehandle.png
│  │  │  │  │  │  uk.svg
│  │  │  │  │  │  user-pc.png
│  │  │  │  │  │  webim.png
│  │  │  │  │  │  workorders.png
│  │  │  │  │  │  
│  │  │  │  │  ├─design
│  │  │  │  │  │      area.gif
│  │  │  │  │  │      bar.gif
│  │  │  │  │  │      calendar.png
│  │  │  │  │  │      flat.gif
│  │  │  │  │  │      four.png
│  │  │  │  │  │      hole.gif
│  │  │  │  │  │      jzt.png
│  │  │  │  │  │      kpi.png
│  │  │  │  │  │      line.gif
│  │  │  │  │  │      loudou.png
│  │  │  │  │  │      map.png
│  │  │  │  │  │      one.png
│  │  │  │  │  │      pie.gif
│  │  │  │  │  │      point.gif
│  │  │  │  │  │      radar.gif
│  │  │  │  │  │      select.png
│  │  │  │  │  │      table.png
│  │  │  │  │  │      text.png
│  │  │  │  │  │      three.png
│  │  │  │  │  │      two.png
│  │  │  │  │  │      
│  │  │  │  │  ├─face
│  │  │  │  │  │      0.gif
│  │  │  │  │  │      1.gif
│  │  │  │  │  │      10.gif
│  │  │  │  │  │      11.gif
│  │  │  │  │  │      12.gif
│  │  │  │  │  │      13.gif
│  │  │  │  │  │      14.gif
│  │  │  │  │  │      15.gif
│  │  │  │  │  │      16.gif
│  │  │  │  │  │      17.gif
│  │  │  │  │  │      18.gif
│  │  │  │  │  │      19.gif
│  │  │  │  │  │      2.gif
│  │  │  │  │  │      20.gif
│  │  │  │  │  │      21.gif
│  │  │  │  │  │      22.gif
│  │  │  │  │  │      23.gif
│  │  │  │  │  │      24.gif
│  │  │  │  │  │      25.gif
│  │  │  │  │  │      26.gif
│  │  │  │  │  │      27.gif
│  │  │  │  │  │      28.gif
│  │  │  │  │  │      29.gif
│  │  │  │  │  │      3.gif
│  │  │  │  │  │      30.gif
│  │  │  │  │  │      31.gif
│  │  │  │  │  │      32.gif
│  │  │  │  │  │      33.gif
│  │  │  │  │  │      34.gif
│  │  │  │  │  │      35.gif
│  │  │  │  │  │      36.gif
│  │  │  │  │  │      37.gif
│  │  │  │  │  │      38.gif
│  │  │  │  │  │      39.gif
│  │  │  │  │  │      4.gif
│  │  │  │  │  │      40.gif
│  │  │  │  │  │      41.gif
│  │  │  │  │  │      42.gif
│  │  │  │  │  │      43.gif
│  │  │  │  │  │      44.gif
│  │  │  │  │  │      45.gif
│  │  │  │  │  │      46.gif
│  │  │  │  │  │      47.gif
│  │  │  │  │  │      48.gif
│  │  │  │  │  │      49.gif
│  │  │  │  │  │      5.gif
│  │  │  │  │  │      50.gif
│  │  │  │  │  │      51.gif
│  │  │  │  │  │      52.gif
│  │  │  │  │  │      53.gif
│  │  │  │  │  │      54.gif
│  │  │  │  │  │      55.gif
│  │  │  │  │  │      56.gif
│  │  │  │  │  │      57.gif
│  │  │  │  │  │      58.gif
│  │  │  │  │  │      59.gif
│  │  │  │  │  │      6.gif
│  │  │  │  │  │      60.gif
│  │  │  │  │  │      61.gif
│  │  │  │  │  │      62.gif
│  │  │  │  │  │      63.gif
│  │  │  │  │  │      64.gif
│  │  │  │  │  │      65.gif
│  │  │  │  │  │      66.gif
│  │  │  │  │  │      67.gif
│  │  │  │  │  │      68.gif
│  │  │  │  │  │      69.gif
│  │  │  │  │  │      7.gif
│  │  │  │  │  │      70.gif
│  │  │  │  │  │      71.gif
│  │  │  │  │  │      8.gif
│  │  │  │  │  │      9.gif
│  │  │  │  │  │      
│  │  │  │  │  ├─im
│  │  │  │  │  │      user.png
│  │  │  │  │  │      
│  │  │  │  │  ├─pay
│  │  │  │  │  │      AliPay.png
│  │  │  │  │  │      BankCardPay.png
│  │  │  │  │  │      WeChatPay.png
│  │  │  │  │  │      
│  │  │  │  │  ├─player
│  │  │  │  │  │      1.png
│  │  │  │  │  │      2.png
│  │  │  │  │  │      3.png
│  │  │  │  │  │      4.png
│  │  │  │  │  │      5.png
│  │  │  │  │  │      6.png
│  │  │  │  │  │      
│  │  │  │  │  ├─skin
│  │  │  │  │  │      1.jpg
│  │  │  │  │  │      2.jpg
│  │  │  │  │  │      3.jpg
│  │  │  │  │  │      4.jpg
│  │  │  │  │  │      5.jpg
│  │  │  │  │  │      
│  │  │  │  │  └─user
│  │  │  │  │          1.png
│  │  │  │  │          2.png
│  │  │  │  │          3.png
│  │  │  │  │          4.png
│  │  │  │  │          5.png
│  │  │  │  │          
│  │  │  │  ├─js
│  │  │  │  │  │  audioplayer.min.js
│  │  │  │  │  │  clipboard.min.js
│  │  │  │  │  │  CSKeFu_Callout.v1.js
│  │  │  │  │  │  CSKeFu_IM.v1.js
│  │  │  │  │  │  CSKeFu_Rest_Request.v1.js
│  │  │  │  │  │  data-set.min.js
│  │  │  │  │  │  echarts.common.min.js
│  │  │  │  │  │  g2.min.js
│  │  │  │  │  │  jquery-1.10.2.min.js
│  │  │  │  │  │  jquery-1.10.2.min.map
│  │  │  │  │  │  jquery-3.1.0.min.js
│  │  │  │  │  │  jquery-ui-1.10.2.custom.min.js
│  │  │  │  │  │  jquery.cookie.min.js
│  │  │  │  │  │  jquery.darktooltip.js
│  │  │  │  │  │  jquery.form.js
│  │  │  │  │  │  jquery.hotkeys.min.js
│  │  │  │  │  │  jquery.nicescroll.min.js
│  │  │  │  │  │  jquery.orgchart.js
│  │  │  │  │  │  kindeditor-suggest.js
│  │  │  │  │  │  lodash-4.17.4.min.js
│  │  │  │  │  │  main.js
│  │  │  │  │  │  maps.js
│  │  │  │  │  │  md5.min.js
│  │  │  │  │  │  moment.min.js
│  │  │  │  │  │  raphael-min.js
│  │  │  │  │  │  template.js
│  │  │  │  │  │  UKeFu-CallOut.js
│  │  │  │  │  │  UKeFu-InfoAcq.js
│  │  │  │  │  │  ukefu-report.chart.js
│  │  │  │  │  │  ukefu-report.design.js
│  │  │  │  │  │  ukefu-report.js
│  │  │  │  │  │  ukefu.js
│  │  │  │  │  │  UKeFu_Util.js
│  │  │  │  │  │  weixinAudio.js
│  │  │  │  │  │  
│  │  │  │  │  ├─ace
│  │  │  │  │  │  │  ace.js
│  │  │  │  │  │  │  ext-chromevox.js
│  │  │  │  │  │  │  ext-elastic_tabstops_lite.js
│  │  │  │  │  │  │  ext-emmet.js
│  │  │  │  │  │  │  ext-error_marker.js
│  │  │  │  │  │  │  ext-keybinding_menu.js
│  │  │  │  │  │  │  ext-language_tools.js
│  │  │  │  │  │  │  ext-modelist.js
│  │  │  │  │  │  │  ext-old_ie.js
│  │  │  │  │  │  │  ext-searchbox.js
│  │  │  │  │  │  │  ext-settings_menu.js
│  │  │  │  │  │  │  ext-spellcheck.js
│  │  │  │  │  │  │  ext-split.js
│  │  │  │  │  │  │  ext-static_highlight.js
│  │  │  │  │  │  │  ext-statusbar.js
│  │  │  │  │  │  │  ext-textarea.js
│  │  │  │  │  │  │  ext-themelist.js
│  │  │  │  │  │  │  ext-whitespace.js
│  │  │  │  │  │  │  keybinding-emacs.js
│  │  │  │  │  │  │  keybinding-vim.js
│  │  │  │  │  │  │  mode-abap.js
│  │  │  │  │  │  │  mode-actionscript.js
│  │  │  │  │  │  │  mode-ada.js
│  │  │  │  │  │  │  mode-apache_conf.js
│  │  │  │  │  │  │  mode-asciidoc.js
│  │  │  │  │  │  │  mode-assembly_x86.js
│  │  │  │  │  │  │  mode-autohotkey.js
│  │  │  │  │  │  │  mode-batchfile.js
│  │  │  │  │  │  │  mode-c9search.js
│  │  │  │  │  │  │  mode-cirru.js
│  │  │  │  │  │  │  mode-clojure.js
│  │  │  │  │  │  │  mode-cobol.js
│  │  │  │  │  │  │  mode-coffee.js
│  │  │  │  │  │  │  mode-coldfusion.js
│  │  │  │  │  │  │  mode-csharp.js
│  │  │  │  │  │  │  mode-css.js
│  │  │  │  │  │  │  mode-curly.js
│  │  │  │  │  │  │  mode-c_cpp.js
│  │  │  │  │  │  │  mode-d.js
│  │  │  │  │  │  │  mode-dart.js
│  │  │  │  │  │  │  mode-diff.js
│  │  │  │  │  │  │  mode-django.js
│  │  │  │  │  │  │  mode-dot.js
│  │  │  │  │  │  │  mode-ejs.js
│  │  │  │  │  │  │  mode-erlang.js
│  │  │  │  │  │  │  mode-forth.js
│  │  │  │  │  │  │  mode-ftl.js
│  │  │  │  │  │  │  mode-gherkin.js
│  │  │  │  │  │  │  mode-glsl.js
│  │  │  │  │  │  │  mode-golang.js
│  │  │  │  │  │  │  mode-groovy.js
│  │  │  │  │  │  │  mode-haml.js
│  │  │  │  │  │  │  mode-handlebars.js
│  │  │  │  │  │  │  mode-haskell.js
│  │  │  │  │  │  │  mode-haxe.js
│  │  │  │  │  │  │  mode-html.js
│  │  │  │  │  │  │  mode-html_completions.js
│  │  │  │  │  │  │  mode-html_ruby.js
│  │  │  │  │  │  │  mode-ini.js
│  │  │  │  │  │  │  mode-jack.js
│  │  │  │  │  │  │  mode-jade.js
│  │  │  │  │  │  │  mode-java.js
│  │  │  │  │  │  │  mode-javascript.js
│  │  │  │  │  │  │  mode-json.js
│  │  │  │  │  │  │  mode-jsoniq.js
│  │  │  │  │  │  │  mode-jsp.js
│  │  │  │  │  │  │  mode-jsx.js
│  │  │  │  │  │  │  mode-julia.js
│  │  │  │  │  │  │  mode-latex.js
│  │  │  │  │  │  │  mode-less.js
│  │  │  │  │  │  │  mode-liquid.js
│  │  │  │  │  │  │  mode-lisp.js
│  │  │  │  │  │  │  mode-livescript.js
│  │  │  │  │  │  │  mode-logiql.js
│  │  │  │  │  │  │  mode-lsl.js
│  │  │  │  │  │  │  mode-lua.js
│  │  │  │  │  │  │  mode-luapage.js
│  │  │  │  │  │  │  mode-lucene.js
│  │  │  │  │  │  │  mode-makefile.js
│  │  │  │  │  │  │  mode-markdown.js
│  │  │  │  │  │  │  mode-matlab.js
│  │  │  │  │  │  │  mode-mel.js
│  │  │  │  │  │  │  mode-mushcode.js
│  │  │  │  │  │  │  mode-mushcode_high_rules.js
│  │  │  │  │  │  │  mode-mysql.js
│  │  │  │  │  │  │  mode-nix.js
│  │  │  │  │  │  │  mode-objectivec.js
│  │  │  │  │  │  │  mode-ocaml.js
│  │  │  │  │  │  │  mode-pascal.js
│  │  │  │  │  │  │  mode-perl.js
│  │  │  │  │  │  │  mode-pgsql.js
│  │  │  │  │  │  │  mode-php.js
│  │  │  │  │  │  │  mode-plain_text.js
│  │  │  │  │  │  │  mode-powershell.js
│  │  │  │  │  │  │  mode-prolog.js
│  │  │  │  │  │  │  mode-properties.js
│  │  │  │  │  │  │  mode-protobuf.js
│  │  │  │  │  │  │  mode-python.js
│  │  │  │  │  │  │  mode-r.js
│  │  │  │  │  │  │  mode-rdoc.js
│  │  │  │  │  │  │  mode-rhtml.js
│  │  │  │  │  │  │  mode-ruby.js
│  │  │  │  │  │  │  mode-rust.js
│  │  │  │  │  │  │  mode-sass.js
│  │  │  │  │  │  │  mode-scad.js
│  │  │  │  │  │  │  mode-scala.js
│  │  │  │  │  │  │  mode-scheme.js
│  │  │  │  │  │  │  mode-scss.js
│  │  │  │  │  │  │  mode-sh.js
│  │  │  │  │  │  │  mode-sjs.js
│  │  │  │  │  │  │  mode-smarty.js
│  │  │  │  │  │  │  mode-snippets.js
│  │  │  │  │  │  │  mode-soy_template.js
│  │  │  │  │  │  │  mode-space.js
│  │  │  │  │  │  │  mode-sql.js
│  │  │  │  │  │  │  mode-stylus.js
│  │  │  │  │  │  │  mode-svg.js
│  │  │  │  │  │  │  mode-tcl.js
│  │  │  │  │  │  │  mode-tex.js
│  │  │  │  │  │  │  mode-text.js
│  │  │  │  │  │  │  mode-textile.js
│  │  │  │  │  │  │  mode-toml.js
│  │  │  │  │  │  │  mode-twig.js
│  │  │  │  │  │  │  mode-typescript.js
│  │  │  │  │  │  │  mode-vbscript.js
│  │  │  │  │  │  │  mode-velocity.js
│  │  │  │  │  │  │  mode-verilog.js
│  │  │  │  │  │  │  mode-vhdl.js
│  │  │  │  │  │  │  mode-xml.js
│  │  │  │  │  │  │  mode-xquery.js
│  │  │  │  │  │  │  mode-yaml.js
│  │  │  │  │  │  │  theme-ambiance.js
│  │  │  │  │  │  │  theme-chaos.js
│  │  │  │  │  │  │  theme-chrome.js
│  │  │  │  │  │  │  theme-clouds.js
│  │  │  │  │  │  │  theme-clouds_midnight.js
│  │  │  │  │  │  │  theme-cobalt.js
│  │  │  │  │  │  │  theme-crimson_editor.js
│  │  │  │  │  │  │  theme-dawn.js
│  │  │  │  │  │  │  theme-dreamweaver.js
│  │  │  │  │  │  │  theme-eclipse.js
│  │  │  │  │  │  │  theme-github.js
│  │  │  │  │  │  │  theme-idle_fingers.js
│  │  │  │  │  │  │  theme-katzenmilch.js
│  │  │  │  │  │  │  theme-kr.js
│  │  │  │  │  │  │  theme-kuroir.js
│  │  │  │  │  │  │  theme-merbivore.js
│  │  │  │  │  │  │  theme-merbivore_soft.js
│  │  │  │  │  │  │  theme-monokai.js
│  │  │  │  │  │  │  theme-mono_industrial.js
│  │  │  │  │  │  │  theme-pastel_on_dark.js
│  │  │  │  │  │  │  theme-solarized_dark.js
│  │  │  │  │  │  │  theme-solarized_light.js
│  │  │  │  │  │  │  theme-terminal.js
│  │  │  │  │  │  │  theme-textmate.js
│  │  │  │  │  │  │  theme-tomorrow.js
│  │  │  │  │  │  │  theme-tomorrow_night.js
│  │  │  │  │  │  │  theme-tomorrow_night_blue.js
│  │  │  │  │  │  │  theme-tomorrow_night_bright.js
│  │  │  │  │  │  │  theme-tomorrow_night_eighties.js
│  │  │  │  │  │  │  theme-twilight.js
│  │  │  │  │  │  │  theme-vibrant_ink.js
│  │  │  │  │  │  │  theme-xcode.js
│  │  │  │  │  │  │  worker-coffee.js
│  │  │  │  │  │  │  worker-css.js
│  │  │  │  │  │  │  worker-html.js
│  │  │  │  │  │  │  worker-javascript.js
│  │  │  │  │  │  │  worker-json.js
│  │  │  │  │  │  │  worker-lua.js
│  │  │  │  │  │  │  worker-php.js
│  │  │  │  │  │  │  worker-xquery.js
│  │  │  │  │  │  │  
│  │  │  │  │  │  └─snippets
│  │  │  │  │  │          abap.js
│  │  │  │  │  │          actionscript.js
│  │  │  │  │  │          ada.js
│  │  │  │  │  │          apache_conf.js
│  │  │  │  │  │          asciidoc.js
│  │  │  │  │  │          assembly_x86.js
│  │  │  │  │  │          autohotkey.js
│  │  │  │  │  │          batchfile.js
│  │  │  │  │  │          c9search.js
│  │  │  │  │  │          cirru.js
│  │  │  │  │  │          clojure.js
│  │  │  │  │  │          cobol.js
│  │  │  │  │  │          coffee.js
│  │  │  │  │  │          coldfusion.js
│  │  │  │  │  │          csharp.js
│  │  │  │  │  │          css.js
│  │  │  │  │  │          curly.js
│  │  │  │  │  │          c_cpp.js
│  │  │  │  │  │          d.js
│  │  │  │  │  │          dart.js
│  │  │  │  │  │          diff.js
│  │  │  │  │  │          django.js
│  │  │  │  │  │          dot.js
│  │  │  │  │  │          ejs.js
│  │  │  │  │  │          erlang.js
│  │  │  │  │  │          forth.js
│  │  │  │  │  │          ftl.js
│  │  │  │  │  │          gherkin.js
│  │  │  │  │  │          glsl.js
│  │  │  │  │  │          golang.js
│  │  │  │  │  │          groovy.js
│  │  │  │  │  │          haml.js
│  │  │  │  │  │          handlebars.js
│  │  │  │  │  │          haskell.js
│  │  │  │  │  │          haxe.js
│  │  │  │  │  │          html.js
│  │  │  │  │  │          html_completions.js
│  │  │  │  │  │          html_ruby.js
│  │  │  │  │  │          ini.js
│  │  │  │  │  │          jack.js
│  │  │  │  │  │          jade.js
│  │  │  │  │  │          java.js
│  │  │  │  │  │          javascript.js
│  │  │  │  │  │          json.js
│  │  │  │  │  │          jsoniq.js
│  │  │  │  │  │          jsp.js
│  │  │  │  │  │          jsx.js
│  │  │  │  │  │          julia.js
│  │  │  │  │  │          latex.js
│  │  │  │  │  │          less.js
│  │  │  │  │  │          liquid.js
│  │  │  │  │  │          lisp.js
│  │  │  │  │  │          livescript.js
│  │  │  │  │  │          logiql.js
│  │  │  │  │  │          lsl.js
│  │  │  │  │  │          lua.js
│  │  │  │  │  │          luapage.js
│  │  │  │  │  │          lucene.js
│  │  │  │  │  │          makefile.js
│  │  │  │  │  │          markdown.js
│  │  │  │  │  │          matlab.js
│  │  │  │  │  │          mel.js
│  │  │  │  │  │          mushcode.js
│  │  │  │  │  │          mushcode_high_rules.js
│  │  │  │  │  │          mysql.js
│  │  │  │  │  │          nix.js
│  │  │  │  │  │          objectivec.js
│  │  │  │  │  │          ocaml.js
│  │  │  │  │  │          pascal.js
│  │  │  │  │  │          perl.js
│  │  │  │  │  │          pgsql.js
│  │  │  │  │  │          php.js
│  │  │  │  │  │          plain_text.js
│  │  │  │  │  │          powershell.js
│  │  │  │  │  │          prolog.js
│  │  │  │  │  │          properties.js
│  │  │  │  │  │          protobuf.js
│  │  │  │  │  │          python.js
│  │  │  │  │  │          r.js
│  │  │  │  │  │          rdoc.js
│  │  │  │  │  │          rhtml.js
│  │  │  │  │  │          ruby.js
│  │  │  │  │  │          rust.js
│  │  │  │  │  │          sass.js
│  │  │  │  │  │          scad.js
│  │  │  │  │  │          scala.js
│  │  │  │  │  │          scheme.js
│  │  │  │  │  │          scss.js
│  │  │  │  │  │          sh.js
│  │  │  │  │  │          sjs.js
│  │  │  │  │  │          smarty.js
│  │  │  │  │  │          snippets.js
│  │  │  │  │  │          soy_template.js
│  │  │  │  │  │          space.js
│  │  │  │  │  │          sql.js
│  │  │  │  │  │          stylus.js
│  │  │  │  │  │          svg.js
│  │  │  │  │  │          tcl.js
│  │  │  │  │  │          tex.js
│  │  │  │  │  │          text.js
│  │  │  │  │  │          textile.js
│  │  │  │  │  │          toml.js
│  │  │  │  │  │          twig.js
│  │  │  │  │  │          typescript.js
│  │  │  │  │  │          vbscript.js
│  │  │  │  │  │          velocity.js
│  │  │  │  │  │          verilog.js
│  │  │  │  │  │          vhdl.js
│  │  │  │  │  │          xml.js
│  │  │  │  │  │          xquery.js
│  │  │  │  │  │          yaml.js
│  │  │  │  │  │          
│  │  │  │  │  ├─kindeditor
│  │  │  │  │  │  │  kindeditor-all-min.js
│  │  │  │  │  │  │  kindeditor-all.js
│  │  │  │  │  │  │  kindeditor-min.js
│  │  │  │  │  │  │  kindeditor.js
│  │  │  │  │  │  │  license.txt
│  │  │  │  │  │  │  
│  │  │  │  │  │  ├─lang
│  │  │  │  │  │  │      ar.js
│  │  │  │  │  │  │      en.js
│  │  │  │  │  │  │      ko.js
│  │  │  │  │  │  │      ru.js
│  │  │  │  │  │  │      zh-CN.js
│  │  │  │  │  │  │      zh-TW.js
│  │  │  │  │  │  │      
│  │  │  │  │  │  ├─plugins
│  │  │  │  │  │  │  ├─anchor
│  │  │  │  │  │  │  │      anchor.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─autoheight
│  │  │  │  │  │  │  │      autoheight.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─baidumap
│  │  │  │  │  │  │  │      baidumap.js
│  │  │  │  │  │  │  │      index.html
│  │  │  │  │  │  │  │      map.html
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─clearhtml
│  │  │  │  │  │  │  │      clearhtml.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─code
│  │  │  │  │  │  │  │      code.js
│  │  │  │  │  │  │  │      prettify.css
│  │  │  │  │  │  │  │      prettify.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─emoticons
│  │  │  │  │  │  │  │  │  emoticons.js
│  │  │  │  │  │  │  │  │  
│  │  │  │  │  │  │  │  └─images
│  │  │  │  │  │  │  │          0.gif
│  │  │  │  │  │  │  │          0.png
│  │  │  │  │  │  │  │          1.gif
│  │  │  │  │  │  │  │          1.png
│  │  │  │  │  │  │  │          10.gif
│  │  │  │  │  │  │  │          10.png
│  │  │  │  │  │  │  │          100.gif
│  │  │  │  │  │  │  │          100.png
│  │  │  │  │  │  │  │          101.gif
│  │  │  │  │  │  │  │          101.png
│  │  │  │  │  │  │  │          102.gif
│  │  │  │  │  │  │  │          102.png
│  │  │  │  │  │  │  │          103.gif
│  │  │  │  │  │  │  │          103.png
│  │  │  │  │  │  │  │          104.gif
│  │  │  │  │  │  │  │          104.png
│  │  │  │  │  │  │  │          11.gif
│  │  │  │  │  │  │  │          11.png
│  │  │  │  │  │  │  │          12.gif
│  │  │  │  │  │  │  │          12.png
│  │  │  │  │  │  │  │          13.gif
│  │  │  │  │  │  │  │          13.png
│  │  │  │  │  │  │  │          14.gif
│  │  │  │  │  │  │  │          14.png
│  │  │  │  │  │  │  │          15.gif
│  │  │  │  │  │  │  │          15.png
│  │  │  │  │  │  │  │          16.gif
│  │  │  │  │  │  │  │          16.png
│  │  │  │  │  │  │  │          17.gif
│  │  │  │  │  │  │  │          17.png
│  │  │  │  │  │  │  │          18.gif
│  │  │  │  │  │  │  │          18.png
│  │  │  │  │  │  │  │          19.gif
│  │  │  │  │  │  │  │          19.png
│  │  │  │  │  │  │  │          2.gif
│  │  │  │  │  │  │  │          2.png
│  │  │  │  │  │  │  │          20.gif
│  │  │  │  │  │  │  │          20.png
│  │  │  │  │  │  │  │          21.gif
│  │  │  │  │  │  │  │          21.png
│  │  │  │  │  │  │  │          22.gif
│  │  │  │  │  │  │  │          22.png
│  │  │  │  │  │  │  │          23.gif
│  │  │  │  │  │  │  │          23.png
│  │  │  │  │  │  │  │          24.gif
│  │  │  │  │  │  │  │          24.png
│  │  │  │  │  │  │  │          25.gif
│  │  │  │  │  │  │  │          25.png
│  │  │  │  │  │  │  │          26.gif
│  │  │  │  │  │  │  │          26.png
│  │  │  │  │  │  │  │          27.gif
│  │  │  │  │  │  │  │          27.png
│  │  │  │  │  │  │  │          28.gif
│  │  │  │  │  │  │  │          28.png
│  │  │  │  │  │  │  │          29.gif
│  │  │  │  │  │  │  │          29.png
│  │  │  │  │  │  │  │          3.gif
│  │  │  │  │  │  │  │          3.png
│  │  │  │  │  │  │  │          30.gif
│  │  │  │  │  │  │  │          30.png
│  │  │  │  │  │  │  │          31.gif
│  │  │  │  │  │  │  │          31.png
│  │  │  │  │  │  │  │          32.gif
│  │  │  │  │  │  │  │          32.png
│  │  │  │  │  │  │  │          33.gif
│  │  │  │  │  │  │  │          33.png
│  │  │  │  │  │  │  │          34.gif
│  │  │  │  │  │  │  │          34.png
│  │  │  │  │  │  │  │          35.gif
│  │  │  │  │  │  │  │          35.png
│  │  │  │  │  │  │  │          36.gif
│  │  │  │  │  │  │  │          36.png
│  │  │  │  │  │  │  │          37.gif
│  │  │  │  │  │  │  │          37.png
│  │  │  │  │  │  │  │          38.gif
│  │  │  │  │  │  │  │          38.png
│  │  │  │  │  │  │  │          39.gif
│  │  │  │  │  │  │  │          39.png
│  │  │  │  │  │  │  │          4.gif
│  │  │  │  │  │  │  │          4.png
│  │  │  │  │  │  │  │          40.gif
│  │  │  │  │  │  │  │          40.png
│  │  │  │  │  │  │  │          41.gif
│  │  │  │  │  │  │  │          41.png
│  │  │  │  │  │  │  │          42.gif
│  │  │  │  │  │  │  │          42.png
│  │  │  │  │  │  │  │          43.gif
│  │  │  │  │  │  │  │          43.png
│  │  │  │  │  │  │  │          44.gif
│  │  │  │  │  │  │  │          44.png
│  │  │  │  │  │  │  │          45.gif
│  │  │  │  │  │  │  │          45.png
│  │  │  │  │  │  │  │          46.gif
│  │  │  │  │  │  │  │          46.png
│  │  │  │  │  │  │  │          47.gif
│  │  │  │  │  │  │  │          47.png
│  │  │  │  │  │  │  │          48.gif
│  │  │  │  │  │  │  │          48.png
│  │  │  │  │  │  │  │          49.gif
│  │  │  │  │  │  │  │          49.png
│  │  │  │  │  │  │  │          5.gif
│  │  │  │  │  │  │  │          5.png
│  │  │  │  │  │  │  │          50.gif
│  │  │  │  │  │  │  │          50.png
│  │  │  │  │  │  │  │          51.gif
│  │  │  │  │  │  │  │          51.png
│  │  │  │  │  │  │  │          52.gif
│  │  │  │  │  │  │  │          52.png
│  │  │  │  │  │  │  │          53.gif
│  │  │  │  │  │  │  │          53.png
│  │  │  │  │  │  │  │          54.gif
│  │  │  │  │  │  │  │          54.png
│  │  │  │  │  │  │  │          55.gif
│  │  │  │  │  │  │  │          55.png
│  │  │  │  │  │  │  │          56.gif
│  │  │  │  │  │  │  │          56.png
│  │  │  │  │  │  │  │          57.gif
│  │  │  │  │  │  │  │          57.png
│  │  │  │  │  │  │  │          58.gif
│  │  │  │  │  │  │  │          58.png
│  │  │  │  │  │  │  │          59.gif
│  │  │  │  │  │  │  │          59.png
│  │  │  │  │  │  │  │          6.gif
│  │  │  │  │  │  │  │          6.png
│  │  │  │  │  │  │  │          60.gif
│  │  │  │  │  │  │  │          60.png
│  │  │  │  │  │  │  │          61.gif
│  │  │  │  │  │  │  │          61.png
│  │  │  │  │  │  │  │          62.gif
│  │  │  │  │  │  │  │          62.png
│  │  │  │  │  │  │  │          63.gif
│  │  │  │  │  │  │  │          63.png
│  │  │  │  │  │  │  │          64.gif
│  │  │  │  │  │  │  │          64.png
│  │  │  │  │  │  │  │          65.gif
│  │  │  │  │  │  │  │          65.png
│  │  │  │  │  │  │  │          66.gif
│  │  │  │  │  │  │  │          66.png
│  │  │  │  │  │  │  │          67.gif
│  │  │  │  │  │  │  │          67.png
│  │  │  │  │  │  │  │          68.gif
│  │  │  │  │  │  │  │          68.png
│  │  │  │  │  │  │  │          69.gif
│  │  │  │  │  │  │  │          69.png
│  │  │  │  │  │  │  │          7.gif
│  │  │  │  │  │  │  │          7.png
│  │  │  │  │  │  │  │          70.gif
│  │  │  │  │  │  │  │          70.png
│  │  │  │  │  │  │  │          71.gif
│  │  │  │  │  │  │  │          71.png
│  │  │  │  │  │  │  │          72.gif
│  │  │  │  │  │  │  │          72.png
│  │  │  │  │  │  │  │          73.gif
│  │  │  │  │  │  │  │          73.png
│  │  │  │  │  │  │  │          74.gif
│  │  │  │  │  │  │  │          74.png
│  │  │  │  │  │  │  │          75.gif
│  │  │  │  │  │  │  │          75.png
│  │  │  │  │  │  │  │          76.gif
│  │  │  │  │  │  │  │          76.png
│  │  │  │  │  │  │  │          77.gif
│  │  │  │  │  │  │  │          77.png
│  │  │  │  │  │  │  │          78.gif
│  │  │  │  │  │  │  │          78.png
│  │  │  │  │  │  │  │          79.gif
│  │  │  │  │  │  │  │          79.png
│  │  │  │  │  │  │  │          8.gif
│  │  │  │  │  │  │  │          8.png
│  │  │  │  │  │  │  │          80.gif
│  │  │  │  │  │  │  │          80.png
│  │  │  │  │  │  │  │          81.gif
│  │  │  │  │  │  │  │          81.png
│  │  │  │  │  │  │  │          82.gif
│  │  │  │  │  │  │  │          82.png
│  │  │  │  │  │  │  │          83.gif
│  │  │  │  │  │  │  │          83.png
│  │  │  │  │  │  │  │          84.gif
│  │  │  │  │  │  │  │          84.png
│  │  │  │  │  │  │  │          85.gif
│  │  │  │  │  │  │  │          85.png
│  │  │  │  │  │  │  │          86.gif
│  │  │  │  │  │  │  │          86.png
│  │  │  │  │  │  │  │          87.gif
│  │  │  │  │  │  │  │          87.png
│  │  │  │  │  │  │  │          88.gif
│  │  │  │  │  │  │  │          88.png
│  │  │  │  │  │  │  │          89.gif
│  │  │  │  │  │  │  │          89.png
│  │  │  │  │  │  │  │          9.gif
│  │  │  │  │  │  │  │          9.png
│  │  │  │  │  │  │  │          90.gif
│  │  │  │  │  │  │  │          90.png
│  │  │  │  │  │  │  │          91.gif
│  │  │  │  │  │  │  │          91.png
│  │  │  │  │  │  │  │          92.gif
│  │  │  │  │  │  │  │          92.png
│  │  │  │  │  │  │  │          93.gif
│  │  │  │  │  │  │  │          93.png
│  │  │  │  │  │  │  │          94.gif
│  │  │  │  │  │  │  │          94.png
│  │  │  │  │  │  │  │          95.gif
│  │  │  │  │  │  │  │          95.png
│  │  │  │  │  │  │  │          96.gif
│  │  │  │  │  │  │  │          96.png
│  │  │  │  │  │  │  │          97.gif
│  │  │  │  │  │  │  │          97.png
│  │  │  │  │  │  │  │          98.gif
│  │  │  │  │  │  │  │          98.png
│  │  │  │  │  │  │  │          99.gif
│  │  │  │  │  │  │  │          99.png
│  │  │  │  │  │  │  │          
│  │  │  │  │  │  │  ├─filemanager
│  │  │  │  │  │  │  │  │  filemanager.js
│  │  │  │  │  │  │  │  │  
│  │  │  │  │  │  │  │  └─images
│  │  │  │  │  │  │  │          file-16.gif
│  │  │  │  │  │  │  │          file-64.gif
│  │  │  │  │  │  │  │          folder-16.gif
│  │  │  │  │  │  │  │          folder-64.gif
│  │  │  │  │  │  │  │          go-up.gif
│  │  │  │  │  │  │  │          
│  │  │  │  │  │  │  ├─fixtoolbar
│  │  │  │  │  │  │  │      fixtoolbar.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─flash
│  │  │  │  │  │  │  │      flash.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─image
│  │  │  │  │  │  │  │  │  image.js
│  │  │  │  │  │  │  │  │  
│  │  │  │  │  │  │  │  └─images
│  │  │  │  │  │  │  │          align_left.gif
│  │  │  │  │  │  │  │          align_right.gif
│  │  │  │  │  │  │  │          align_top.gif
│  │  │  │  │  │  │  │          refresh.png
│  │  │  │  │  │  │  │          
│  │  │  │  │  │  │  ├─insertfile
│  │  │  │  │  │  │  │      insertfile.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─lineheight
│  │  │  │  │  │  │  │      lineheight.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─link
│  │  │  │  │  │  │  │      link.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─map
│  │  │  │  │  │  │  │      map.html
│  │  │  │  │  │  │  │      map.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─media
│  │  │  │  │  │  │  │      media.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─multiimage
│  │  │  │  │  │  │  │  │  multiimage.js
│  │  │  │  │  │  │  │  │  
│  │  │  │  │  │  │  │  └─images
│  │  │  │  │  │  │  │          image.png
│  │  │  │  │  │  │  │          select-files-en.png
│  │  │  │  │  │  │  │          select-files-zh-CN.png
│  │  │  │  │  │  │  │          swfupload.swf
│  │  │  │  │  │  │  │          
│  │  │  │  │  │  │  ├─pagebreak
│  │  │  │  │  │  │  │      pagebreak.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─plainpaste
│  │  │  │  │  │  │  │      plainpaste.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─preview
│  │  │  │  │  │  │  │      preview.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─quickformat
│  │  │  │  │  │  │  │      quickformat.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─table
│  │  │  │  │  │  │  │      table.js
│  │  │  │  │  │  │  │      
│  │  │  │  │  │  │  ├─template
│  │  │  │  │  │  │  │  │  template.js
│  │  │  │  │  │  │  │  │  
│  │  │  │  │  │  │  │  └─html
│  │  │  │  │  │  │  │          1.html
│  │  │  │  │  │  │  │          2.html
│  │  │  │  │  │  │  │          3.html
│  │  │  │  │  │  │  │          
│  │  │  │  │  │  │  └─wordpaste
│  │  │  │  │  │  │          wordpaste.js
│  │  │  │  │  │  │          
│  │  │  │  │  │  └─themes
│  │  │  │  │  │      ├─common
│  │  │  │  │  │      │      anchor.gif
│  │  │  │  │  │      │      blank.gif
│  │  │  │  │  │      │      flash.gif
│  │  │  │  │  │      │      loading.gif
│  │  │  │  │  │      │      media.gif
│  │  │  │  │  │      │      rm.gif
│  │  │  │  │  │      │      
│  │  │  │  │  │      ├─default
│  │  │  │  │  │      │      background.png
│  │  │  │  │  │      │      default.css
│  │  │  │  │  │      │      default.png
│  │  │  │  │  │      │      
│  │  │  │  │  │      ├─qq
│  │  │  │  │  │      │      editor.gif
│  │  │  │  │  │      │      qq.css
│  │  │  │  │  │      │      
│  │  │  │  │  │      └─simple
│  │  │  │  │  │              simple.css
│  │  │  │  │  │              
│  │  │  │  │  ├─scrawl
│  │  │  │  │  │  │  scrawl.js
│  │  │  │  │  │  │  
│  │  │  │  │  │  └─css
│  │  │  │  │  │      │  scrawl.css
│  │  │  │  │  │      │  
│  │  │  │  │  │      └─images
│  │  │  │  │  │              addimg.png
│  │  │  │  │  │              blur.png
│  │  │  │  │  │              brush.png
│  │  │  │  │  │              delimg.png
│  │  │  │  │  │              delimgH.png
│  │  │  │  │  │              empty.png
│  │  │  │  │  │              emptyH.png
│  │  │  │  │  │              eraser.png
│  │  │  │  │  │              init.png
│  │  │  │  │  │              redo.png
│  │  │  │  │  │              redoH.png
│  │  │  │  │  │              save.png
│  │  │  │  │  │              scale.png
│  │  │  │  │  │              scaleH.png
│  │  │  │  │  │              size.png
│  │  │  │  │  │              undo.png
│  │  │  │  │  │              undoH.png
│  │  │  │  │  │              
│  │  │  │  │  ├─select
│  │  │  │  │  │  ├─css
│  │  │  │  │  │  │      select2.css
│  │  │  │  │  │  │      select2.min.css
│  │  │  │  │  │  │      
│  │  │  │  │  │  └─js
│  │  │  │  │  │      │  select2.full.js
│  │  │  │  │  │      │  select2.full.min.js
│  │  │  │  │  │      │  select2.js
│  │  │  │  │  │      │  select2.min.js
│  │  │  │  │  │      │  
│  │  │  │  │  │      └─i18n
│  │  │  │  │  │              ar.js
│  │  │  │  │  │              az.js
│  │  │  │  │  │              bg.js
│  │  │  │  │  │              ca.js
│  │  │  │  │  │              cs.js
│  │  │  │  │  │              da.js
│  │  │  │  │  │              de.js
│  │  │  │  │  │              el.js
│  │  │  │  │  │              en.js
│  │  │  │  │  │              es.js
│  │  │  │  │  │              et.js
│  │  │  │  │  │              eu.js
│  │  │  │  │  │              fa.js
│  │  │  │  │  │              fi.js
│  │  │  │  │  │              fr.js
│  │  │  │  │  │              gl.js
│  │  │  │  │  │              he.js
│  │  │  │  │  │              hi.js
│  │  │  │  │  │              hr.js
│  │  │  │  │  │              hu.js
│  │  │  │  │  │              id.js
│  │  │  │  │  │              is.js
│  │  │  │  │  │              it.js
│  │  │  │  │  │              ja.js
│  │  │  │  │  │              km.js
│  │  │  │  │  │              ko.js
│  │  │  │  │  │              lt.js
│  │  │  │  │  │              lv.js
│  │  │  │  │  │              mk.js
│  │  │  │  │  │              ms.js
│  │  │  │  │  │              nb.js
│  │  │  │  │  │              nl.js
│  │  │  │  │  │              pl.js
│  │  │  │  │  │              pt-BR.js
│  │  │  │  │  │              pt.js
│  │  │  │  │  │              ro.js
│  │  │  │  │  │              ru.js
│  │  │  │  │  │              sk.js
│  │  │  │  │  │              sr-Cyrl.js
│  │  │  │  │  │              sr.js
│  │  │  │  │  │              sv.js
│  │  │  │  │  │              th.js
│  │  │  │  │  │              tr.js
│  │  │  │  │  │              uk.js
│  │  │  │  │  │              vi.js
│  │  │  │  │  │              zh-CN.js
│  │  │  │  │  │              zh-TW.js
│  │  │  │  │  │              
│  │  │  │  │  ├─snaker
│  │  │  │  │  │  │  dialog.js
│  │  │  │  │  │  │  snaker.designer.js
│  │  │  │  │  │  │  snaker.designer.min.js
│  │  │  │  │  │  │  snaker.designer.src.js
│  │  │  │  │  │  │  snaker.editors.js
│  │  │  │  │  │  │  snaker.form.js
│  │  │  │  │  │  │  snaker.model.js
│  │  │  │  │  │  │  snaker.util.js
│  │  │  │  │  │  │  
│  │  │  │  │  │  └─images
│  │  │  │  │  │      │  add.png
│  │  │  │  │  │      │  admin_public.png
│  │  │  │  │  │      │  admin_repeat_x.png
│  │  │  │  │  │      │  aqtc.png
│  │  │  │  │  │      │  arrow.png
│  │  │  │  │  │      │  arrow_turn_left.png
│  │  │  │  │  │      │  arrow_undo.png
│  │  │  │  │  │      │  bg.jpg
│  │  │  │  │  │      │  bg.png
│  │  │  │  │  │      │  button_100px.gif
│  │  │  │  │  │      │  button_38px.gif
│  │  │  │  │  │      │  button_70px.gif
│  │  │  │  │  │      │  button_gray38px.gif
│  │  │  │  │  │      │  button_gray70px.gif
│  │  │  │  │  │      │  bz.png
│  │  │  │  │  │      │  clock.png
│  │  │  │  │  │      │  close.jpg
│  │  │  │  │  │      │  config.png
│  │  │  │  │  │      │  config_add.png
│  │  │  │  │  │      │  delete.png
│  │  │  │  │  │      │  designer.png
│  │  │  │  │  │      │  edit.png
│  │  │  │  │  │      │  flag_read.png
│  │  │  │  │  │      │  flow-start.png
│  │  │  │  │  │      │  flowclose.gif
│  │  │  │  │  │      │  flowopen.gif
│  │  │  │  │  │      │  formedit.png
│  │  │  │  │  │      │  form_edit.png
│  │  │  │  │  │      │  goto.png
│  │  │  │  │  │      │  headerbg.gif
│  │  │  │  │  │      │  home.png
│  │  │  │  │  │      │  logo.png
│  │  │  │  │  │      │  logout.png
│  │  │  │  │  │      │  modpwd.png
│  │  │  │  │  │      │  open.jpg
│  │  │  │  │  │      │  picture_link.png
│  │  │  │  │  │      │  save.gif
│  │  │  │  │  │      │  select.png
│  │  │  │  │  │      │  select16.gif
│  │  │  │  │  │      │  snaker.png
│  │  │  │  │  │      │  sorting-asc.png
│  │  │  │  │  │      │  sorting-desc.png
│  │  │  │  │  │      │  sorting.png
│  │  │  │  │  │      │  switch_left.gif
│  │  │  │  │  │      │  switch_right.gif
│  │  │  │  │  │      │  top_bg.jpg
│  │  │  │  │  │      │  user.png
│  │  │  │  │  │      │  view.png
│  │  │  │  │  │      │  
│  │  │  │  │  │      ├─16
│  │  │  │  │  │      │      delete.gif
│  │  │  │  │  │      │      end_event_cancel.png
│  │  │  │  │  │      │      end_event_error.png
│  │  │  │  │  │      │      end_event_terminate.png
│  │  │  │  │  │      │      event.gif
│  │  │  │  │  │      │      events_multiple.gif
│  │  │  │  │  │      │      flow_sequence.png
│  │  │  │  │  │      │      gateway_exclusive.png
│  │  │  │  │  │      │      gateway_parallel.png
│  │  │  │  │  │      │      new_event_listener.gif
│  │  │  │  │  │      │      new_swimlane.gif
│  │  │  │  │  │      │      new_timer.gif
│  │  │  │  │  │      │      node_elements_multiple.gif
│  │  │  │  │  │      │      start_event_empty.png
│  │  │  │  │  │      │      swimlane.gif
│  │  │  │  │  │      │      swimlanes_multiple.gif
│  │  │  │  │  │      │      task_empty.png
│  │  │  │  │  │      │      task_hql.png
│  │  │  │  │  │      │      task_java.png
│  │  │  │  │  │      │      task_sql.png
│  │  │  │  │  │      │      task_wait.png
│  │  │  │  │  │      │      timer.gif
│  │  │  │  │  │      │      timers_multiple.gif
│  │  │  │  │  │      │      transitions_multiple.gif
│  │  │  │  │  │      │      
│  │  │  │  │  │      ├─48
│  │  │  │  │  │      │      end_event_cancel.png
│  │  │  │  │  │      │      end_event_error.png
│  │  │  │  │  │      │      end_event_terminate.png
│  │  │  │  │  │      │      flow_sequence.png
│  │  │  │  │  │      │      gateway_exclusive.png
│  │  │  │  │  │      │      gateway_parallel.png
│  │  │  │  │  │      │      start_event_empty.png
│  │  │  │  │  │      │      task_empty.png
│  │  │  │  │  │      │      task_hql.png
│  │  │  │  │  │      │      task_java.png
│  │  │  │  │  │      │      task_sql.png
│  │  │  │  │  │      │      task_wait.png
│  │  │  │  │  │      │      
│  │  │  │  │  │      ├─field
│  │  │  │  │  │      │      field_attachment.gif
│  │  │  │  │  │      │      field_check.png
│  │  │  │  │  │      │      field_date.png
│  │  │  │  │  │      │      field_department.png
│  │  │  │  │  │      │      field_editor.png
│  │  │  │  │  │      │      field_radio.png
│  │  │  │  │  │      │      field_select.png
│  │  │  │  │  │      │      field_table.png
│  │  │  │  │  │      │      field_textarea.gif
│  │  │  │  │  │      │      field_textfield.png
│  │  │  │  │  │      │      field_user.png
│  │  │  │  │  │      │      
│  │  │  │  │  │      └─form
│  │  │  │  │  │              save.png
│  │  │  │  │  │              
│  │  │  │  │  ├─theme
│  │  │  │  │  │      macarons.js
│  │  │  │  │  │      shine.js
│  │  │  │  │  │      wonderland.js
│  │  │  │  │  │      
│  │  │  │  │  ├─timelineMe
│  │  │  │  │  │      jquery.timelineMe.js
│  │  │  │  │  │      
│  │  │  │  │  └─ztree
│  │  │  │  │      │  jquery.ztree.all.min.js
│  │  │  │  │      │  
│  │  │  │  │      └─zTreeStyle
│  │  │  │  │          │  zTreeStyle.css
│  │  │  │  │          │  
│  │  │  │  │          └─img
│  │  │  │  │              │  line_conn.gif
│  │  │  │  │              │  loading.gif
│  │  │  │  │              │  zTreeStandard.gif
│  │  │  │  │              │  zTreeStandard.png
│  │  │  │  │              │  
│  │  │  │  │              └─diy
│  │  │  │  │                      1_close.png
│  │  │  │  │                      1_open.png
│  │  │  │  │                      2.png
│  │  │  │  │                      3.png
│  │  │  │  │                      4.png
│  │  │  │  │                      5.png
│  │  │  │  │                      6.png
│  │  │  │  │                      7.png
│  │  │  │  │                      8.png
│  │  │  │  │                      9.png
│  │  │  │  │                      
│  │  │  │  ├─lay
│  │  │  │  │  ├─dest
│  │  │  │  │  │      layui.all.js
│  │  │  │  │  │      
│  │  │  │  │  └─modules
│  │  │  │  │          code.js
│  │  │  │  │          element.js
│  │  │  │  │          flow.js
│  │  │  │  │          form.js
│  │  │  │  │          jquery.js
│  │  │  │  │          laydate.js
│  │  │  │  │          layedit.js
│  │  │  │  │          layer.js
│  │  │  │  │          laypage.js
│  │  │  │  │          laytpl.js
│  │  │  │  │          mobile.js
│  │  │  │  │          tree.js
│  │  │  │  │          upload.js
│  │  │  │  │          util.js
│  │  │  │  │          
│  │  │  │  ├─layui
│  │  │  │  │  │  layui.all.js
│  │  │  │  │  │  layui.js
│  │  │  │  │  │  
│  │  │  │  │  ├─css
│  │  │  │  │  │  │  layui.css
│  │  │  │  │  │  │  layui.mobile.css
│  │  │  │  │  │  │  
│  │  │  │  │  │  └─modules
│  │  │  │  │  │      │  code.css
│  │  │  │  │  │      │  
│  │  │  │  │  │      ├─laydate
│  │  │  │  │  │      │  └─default
│  │  │  │  │  │      │          laydate.css
│  │  │  │  │  │      │          
│  │  │  │  │  │      └─layer
│  │  │  │  │  │          └─default
│  │  │  │  │  │                  icon-ext.png
│  │  │  │  │  │                  icon.png
│  │  │  │  │  │                  layer.css
│  │  │  │  │  │                  loading-0.gif
│  │  │  │  │  │                  loading-1.gif
│  │  │  │  │  │                  loading-2.gif
│  │  │  │  │  │                  
│  │  │  │  │  ├─font
│  │  │  │  │  │      iconfont.eot
│  │  │  │  │  │      iconfont.svg
│  │  │  │  │  │      iconfont.ttf
│  │  │  │  │  │      iconfont.woff
│  │  │  │  │  │      
│  │  │  │  │  ├─images
│  │  │  │  │  │  └─face
│  │  │  │  │  │          0.gif
│  │  │  │  │  │          1.gif
│  │  │  │  │  │          10.gif
│  │  │  │  │  │          11.gif
│  │  │  │  │  │          12.gif
│  │  │  │  │  │          13.gif
│  │  │  │  │  │          14.gif
│  │  │  │  │  │          15.gif
│  │  │  │  │  │          16.gif
│  │  │  │  │  │          17.gif
│  │  │  │  │  │          18.gif
│  │  │  │  │  │          19.gif
│  │  │  │  │  │          2.gif
│  │  │  │  │  │          20.gif
│  │  │  │  │  │          21.gif
│  │  │  │  │  │          22.gif
│  │  │  │  │  │          23.gif
│  │  │  │  │  │          24.gif
│  │  │  │  │  │          25.gif
│  │  │  │  │  │          26.gif
│  │  │  │  │  │          27.gif
│  │  │  │  │  │          28.gif
│  │  │  │  │  │          29.gif
│  │  │  │  │  │          3.gif
│  │  │  │  │  │          30.gif
│  │  │  │  │  │          31.gif
│  │  │  │  │  │          32.gif
│  │  │  │  │  │          33.gif
│  │  │  │  │  │          34.gif
│  │  │  │  │  │          35.gif
│  │  │  │  │  │          36.gif
│  │  │  │  │  │          37.gif
│  │  │  │  │  │          38.gif
│  │  │  │  │  │          39.gif
│  │  │  │  │  │          4.gif
│  │  │  │  │  │          40.gif
│  │  │  │  │  │          41.gif
│  │  │  │  │  │          42.gif
│  │  │  │  │  │          43.gif
│  │  │  │  │  │          44.gif
│  │  │  │  │  │          45.gif
│  │  │  │  │  │          46.gif
│  │  │  │  │  │          47.gif
│  │  │  │  │  │          48.gif
│  │  │  │  │  │          49.gif
│  │  │  │  │  │          5.gif
│  │  │  │  │  │          50.gif
│  │  │  │  │  │          51.gif
│  │  │  │  │  │          52.gif
│  │  │  │  │  │          53.gif
│  │  │  │  │  │          54.gif
│  │  │  │  │  │          55.gif
│  │  │  │  │  │          56.gif
│  │  │  │  │  │          57.gif
│  │  │  │  │  │          58.gif
│  │  │  │  │  │          59.gif
│  │  │  │  │  │          6.gif
│  │  │  │  │  │          60.gif
│  │  │  │  │  │          61.gif
│  │  │  │  │  │          62.gif
│  │  │  │  │  │          63.gif
│  │  │  │  │  │          64.gif
│  │  │  │  │  │          65.gif
│  │  │  │  │  │          66.gif
│  │  │  │  │  │          67.gif
│  │  │  │  │  │          68.gif
│  │  │  │  │  │          69.gif
│  │  │  │  │  │          7.gif
│  │  │  │  │  │          70.gif
│  │  │  │  │  │          71.gif
│  │  │  │  │  │          8.gif
│  │  │  │  │  │          9.gif
│  │  │  │  │  │          
│  │  │  │  │  └─lay
│  │  │  │  │      └─modules
│  │  │  │  │              carousel.js
│  │  │  │  │              code.js
│  │  │  │  │              element.js
│  │  │  │  │              flow.js
│  │  │  │  │              form.js
│  │  │  │  │              jquery.js
│  │  │  │  │              laydate.js
│  │  │  │  │              layer.js
│  │  │  │  │              laypage.js
│  │  │  │  │              laytpl.js
│  │  │  │  │              mobile.js
│  │  │  │  │              table.js
│  │  │  │  │              tree.js
│  │  │  │  │              upload.js
│  │  │  │  │              util.js
│  │  │  │  │              
│  │  │  │  └─ukfont
│  │  │  │          iconfont.eot
│  │  │  │          iconfont.svg
│  │  │  │          iconfont.ttf
│  │  │  │          iconfont.woff
│  │  │  │          
│  │  │  └─templates
│  │  │      │  login.html
│  │  │      │  register.html
│  │  │      │  
│  │  │      ├─admin
│  │  │      │  │  content.html
│  │  │      │  │  
│  │  │      │  ├─area
│  │  │      │  │      add.html
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      
│  │  │      │  ├─callcenter
│  │  │      │  │  │  index.html
│  │  │      │  │  │  
│  │  │      │  │  ├─acl
│  │  │      │  │  │      add.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─black
│  │  │      │  │  │      add.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─config
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─extention
│  │  │      │  │  │      add.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      ivr.html
│  │  │      │  │  │      
│  │  │      │  │  ├─ivr
│  │  │      │  │  │      design.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─media
│  │  │      │  │  │      add.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      play.html
│  │  │      │  │  │      
│  │  │      │  │  ├─onextention
│  │  │      │  │  │      detail.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─pbxhost
│  │  │      │  │  │      add.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─resource
│  │  │      │  │  │      back.html
│  │  │      │  │  │      config.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      pbxhost.html
│  │  │      │  │  │      
│  │  │      │  │  ├─router
│  │  │      │  │  │      add.html
│  │  │      │  │  │      code.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─siptrunk
│  │  │      │  │  │      add.html
│  │  │      │  │  │      code.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  └─skill
│  │  │      │  │          add.html
│  │  │      │  │          edit.html
│  │  │      │  │          imp.html
│  │  │      │  │          index.html
│  │  │      │  │          
│  │  │      │  ├─channel
│  │  │      │  │  ├─callout
│  │  │      │  │  │      add.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  └─im
│  │  │      │  │          add.html
│  │  │      │  │          edit.html
│  │  │      │  │          index.html
│  │  │      │  │          
│  │  │      │  ├─config
│  │  │      │  │      index.html
│  │  │      │  │      
│  │  │      │  ├─email
│  │  │      │  │      add.html
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      
│  │  │      │  ├─include
│  │  │      │  │      left.html
│  │  │      │  │      tpl.html
│  │  │      │  │      
│  │  │      │  ├─organ
│  │  │      │  │      add.html
│  │  │      │  │      area.html
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      seluser.html
│  │  │      │  │      
│  │  │      │  ├─pay
│  │  │      │  │  ├─payAccount
│  │  │      │  │  │      add.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      update.html
│  │  │      │  │  │      uploadQRCode.html
│  │  │      │  │  │      
│  │  │      │  │  └─payMsg
│  │  │      │  │          add.html
│  │  │      │  │          index.html
│  │  │      │  │          update.html
│  │  │      │  │          
│  │  │      │  ├─role
│  │  │      │  │      add.html
│  │  │      │  │      auth.html
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      seluser.html
│  │  │      │  │      
│  │  │      │  ├─skill
│  │  │      │  │      add.html
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      seluser.html
│  │  │      │  │      
│  │  │      │  ├─sms
│  │  │      │  │      add.html
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      
│  │  │      │  ├─system
│  │  │      │  │  ├─auth
│  │  │      │  │  │      event.html
│  │  │      │  │  │      
│  │  │      │  │  ├─log
│  │  │      │  │  │      detail.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      levels.html
│  │  │      │  │  │      
│  │  │      │  │  ├─metadata
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      imptb.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      table.html
│  │  │      │  │  │      tpedit.html
│  │  │      │  │  │      
│  │  │      │  │  ├─monitor
│  │  │      │  │  │      hazelcast.html
│  │  │      │  │  │      
│  │  │      │  │  ├─sysdic
│  │  │      │  │  │      add.html
│  │  │      │  │  │      batadd.html
│  │  │      │  │  │      dicitem.html
│  │  │      │  │  │      dicitemadd.html
│  │  │      │  │  │      dicitemedit.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  └─template
│  │  │      │  │          add.html
│  │  │      │  │          code.html
│  │  │      │  │          edit.html
│  │  │      │  │          imp.html
│  │  │      │  │          index.html
│  │  │      │  │          list.html
│  │  │      │  │          
│  │  │      │  ├─user
│  │  │      │  │      add.html
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      
│  │  │      │  └─webim
│  │  │      │          index.html
│  │  │      │          invote.html
│  │  │      │          profile.html
│  │  │      │          textpoint.html
│  │  │      │          
│  │  │      ├─apps
│  │  │      │  │  index.html
│  │  │      │  │  index2.html
│  │  │      │  │  
│  │  │      │  ├─agent
│  │  │      │  │  │  agentusers.html
│  │  │      │  │  │  blacklistadd.html
│  │  │      │  │  │  contacts.html
│  │  │      │  │  │  index.html
│  │  │      │  │  │  mainagentuser.html
│  │  │      │  │  │  mainagentuser_callout.html
│  │  │      │  │  │  othertopic.html
│  │  │      │  │  │  quicklist.html
│  │  │      │  │  │  quickreplycontent.html
│  │  │      │  │  │  summary.html
│  │  │      │  │  │  topicdetail.html
│  │  │      │  │  │  transfer.html
│  │  │      │  │  │  transferagentlist.html
│  │  │      │  │  │  upload.html
│  │  │      │  │  │  workorders.html
│  │  │      │  │  │  
│  │  │      │  │  ├─calloutcontact
│  │  │      │  │  │      add.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      
│  │  │      │  │  ├─channel
│  │  │      │  │  │      phone.html
│  │  │      │  │  │      webim.html
│  │  │      │  │  │      weixin.html
│  │  │      │  │  │      
│  │  │      │  │  ├─media
│  │  │      │  │  │      message.html
│  │  │      │  │  │      messageimage.html
│  │  │      │  │  │      
│  │  │      │  │  └─quickreply
│  │  │      │  │          add.html
│  │  │      │  │          addtype.html
│  │  │      │  │          edit.html
│  │  │      │  │          edittype.html
│  │  │      │  │          
│  │  │      │  ├─business
│  │  │      │  │  ├─callcenter
│  │  │      │  │  │  │  notfound.html
│  │  │      │  │  │  │  template.html
│  │  │      │  │  │  │  
│  │  │      │  │  │  ├─configure
│  │  │      │  │  │  │      acl.html
│  │  │      │  │  │  │      callcenter.html
│  │  │      │  │  │  │      external.html
│  │  │      │  │  │  │      ivr.html
│  │  │      │  │  │  │      
│  │  │      │  │  │  ├─dialplan
│  │  │      │  │  │  │      index.html
│  │  │      │  │  │  │      
│  │  │      │  │  │  └─extention
│  │  │      │  │  │          agent.html
│  │  │      │  │  │          detail.html
│  │  │      │  │  │          index.html
│  │  │      │  │  │          ivr.html
│  │  │      │  │  │          siptrunk.html
│  │  │      │  │  │          
│  │  │      │  │  ├─contacts
│  │  │      │  │  │  │  add.html
│  │  │      │  │  │  │  detail.html
│  │  │      │  │  │  │  edit.html
│  │  │      │  │  │  │  imp.html
│  │  │      │  │  │  │  index.html
│  │  │      │  │  │  │  top.html
│  │  │      │  │  │  │  
│  │  │      │  │  │  ├─embed
│  │  │      │  │  │  │      add.html
│  │  │      │  │  │  │      edit.html
│  │  │      │  │  │  │      index.html
│  │  │      │  │  │  │      pages.html
│  │  │      │  │  │  │      
│  │  │      │  │  │  └─include
│  │  │      │  │  │          left.html
│  │  │      │  │  │          
│  │  │      │  │  ├─customer
│  │  │      │  │  │  │  add.html
│  │  │      │  │  │  │  edit.html
│  │  │      │  │  │  │  imp.html
│  │  │      │  │  │  │  index.html
│  │  │      │  │  │  │  top.html
│  │  │      │  │  │  │  
│  │  │      │  │  │  └─include
│  │  │      │  │  │          left.html
│  │  │      │  │  │          
│  │  │      │  │  ├─job
│  │  │      │  │  │      setting.html
│  │  │      │  │  │      
│  │  │      │  │  ├─kbs
│  │  │      │  │  │      add.html
│  │  │      │  │  │      addtype.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      list.html
│  │  │      │  │  │      typelist.html
│  │  │      │  │  │      
│  │  │      │  │  ├─report
│  │  │      │  │  │  │  add.html
│  │  │      │  │  │  │  addtype.html
│  │  │      │  │  │  │  edit.html
│  │  │      │  │  │  │  edittype.html
│  │  │      │  │  │  │  element.html
│  │  │      │  │  │  │  imp.html
│  │  │      │  │  │  │  index.html
│  │  │      │  │  │  │  pbreportindex.html
│  │  │      │  │  │  │  pbreportlist.html
│  │  │      │  │  │  │  replylist.html
│  │  │      │  │  │  │  reportpublish.html
│  │  │      │  │  │  │  view.html
│  │  │      │  │  │  │  
│  │  │      │  │  │  ├─cube
│  │  │      │  │  │  │  │  add.html
│  │  │      │  │  │  │  │  cubepublish.html
│  │  │      │  │  │  │  │  detail.html
│  │  │      │  │  │  │  │  edit.html
│  │  │      │  │  │  │  │  index.html
│  │  │      │  │  │  │  │  list.html
│  │  │      │  │  │  │  │  pbCubeIndex.html
│  │  │      │  │  │  │  │  pbcubelist.html
│  │  │      │  │  │  │  │  
│  │  │      │  │  │  │  ├─cubelevel
│  │  │      │  │  │  │  │      add.html
│  │  │      │  │  │  │  │      edit.html
│  │  │      │  │  │  │  │      fktableiddiv.html
│  │  │      │  │  │  │  │      
│  │  │      │  │  │  │  ├─cubemeasure
│  │  │      │  │  │  │  │      add.html
│  │  │      │  │  │  │  │      edit.html
│  │  │      │  │  │  │  │      fktableiddiv.html
│  │  │      │  │  │  │  │      
│  │  │      │  │  │  │  ├─cubemetadata
│  │  │      │  │  │  │  │      edit.html
│  │  │      │  │  │  │  │      imptb.html
│  │  │      │  │  │  │  │      
│  │  │      │  │  │  │  ├─dimension
│  │  │      │  │  │  │  │      add.html
│  │  │      │  │  │  │  │      edit.html
│  │  │      │  │  │  │  │      fktableiddiv.html
│  │  │      │  │  │  │  │      
│  │  │      │  │  │  │  ├─include
│  │  │      │  │  │  │  │      left.html
│  │  │      │  │  │  │  │      leftforpb.html
│  │  │      │  │  │  │  │      
│  │  │      │  │  │  │  └─type
│  │  │      │  │  │  │          add.html
│  │  │      │  │  │  │          edit.html
│  │  │      │  │  │  │          
│  │  │      │  │  │  └─design
│  │  │      │  │  │      │  add.html
│  │  │      │  │  │      │  element.html
│  │  │      │  │  │      │  elementajax.html
│  │  │      │  │  │      │  elementnoajax.html
│  │  │      │  │  │      │  filter.html
│  │  │      │  │  │      │  filteredit.html
│  │  │      │  │  │      │  fktableid.html
│  │  │      │  │  │      │  index.html
│  │  │      │  │  │      │  layout.html
│  │  │      │  │  │      │  modeldesign.html
│  │  │      │  │  │      │  template.html
│  │  │      │  │  │      │  
│  │  │      │  │  │      ├─cube
│  │  │      │  │  │      │      leftforpb.html
│  │  │      │  │  │      │      pbCubeIndex.html
│  │  │      │  │  │      │      pbcubelist.html
│  │  │      │  │  │      │      
│  │  │      │  │  │      └─modeldesign
│  │  │      │  │  │              add.html
│  │  │      │  │  │              editmodelname.html
│  │  │      │  │  │              filter.html
│  │  │      │  │  │              filteradd.html
│  │  │      │  │  │              filteredit.html
│  │  │      │  │  │              filterlist.html
│  │  │      │  │  │              fktableid.html
│  │  │      │  │  │              fktableiddiv.html
│  │  │      │  │  │              left.html
│  │  │      │  │  │              measureedit.html
│  │  │      │  │  │              right.html
│  │  │      │  │  │              
│  │  │      │  │  ├─topic
│  │  │      │  │  │      add.html
│  │  │      │  │  │      addtype.html
│  │  │      │  │  │      area.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      edittype.html
│  │  │      │  │  │      imp.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  └─view
│  │  │      │  │      │  index.html
│  │  │      │  │      │  
│  │  │      │  │      └─include
│  │  │      │  │              left.html
│  │  │      │  │              
│  │  │      │  ├─callout
│  │  │      │  │  │  index.html
│  │  │      │  │  │  
│  │  │      │  │  ├─dialplan
│  │  │      │  │  │      add.html
│  │  │      │  │  │      archive.html
│  │  │      │  │  │      edit.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─include
│  │  │      │  │  │      left.html
│  │  │      │  │  │      
│  │  │      │  │  ├─reports
│  │  │      │  │  │      agent-monitor.html
│  │  │      │  │  │      agents.html
│  │  │      │  │  │      communicate.html
│  │  │      │  │  │      daily-summary.html
│  │  │      │  │  │      dialplan.html
│  │  │      │  │  │      recording.html
│  │  │      │  │  │      system.html
│  │  │      │  │  │      
│  │  │      │  │  └─resources
│  │  │      │  │          agents.html
│  │  │      │  │          skillgroups.html
│  │  │      │  │          switchboard.html
│  │  │      │  │          
│  │  │      │  ├─chatbot
│  │  │      │  │      edit.html
│  │  │      │  │      index.html
│  │  │      │  │      
│  │  │      │  ├─desktop
│  │  │      │  │  │  index.html
│  │  │      │  │  │  onlineuser.html
│  │  │      │  │  │  profile.html
│  │  │      │  │  │  
│  │  │      │  │  └─include
│  │  │      │  │          left.html
│  │  │      │  │          
│  │  │      │  ├─entim
│  │  │      │  │  │  chat.html
│  │  │      │  │  │  index.html
│  │  │      │  │  │  point.html
│  │  │      │  │  │  skin.html
│  │  │      │  │  │  
│  │  │      │  │  ├─group
│  │  │      │  │  │      grouplist.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      tipmsg.html
│  │  │      │  │  │      user.html
│  │  │      │  │  │      
│  │  │      │  │  └─include
│  │  │      │  │          tpl.html
│  │  │      │  │          
│  │  │      │  ├─im
│  │  │      │  │  │  collecting.html
│  │  │      │  │  │  index.html
│  │  │      │  │  │  leavemsg.html
│  │  │      │  │  │  leavemsgsave.html
│  │  │      │  │  │  mobile.html
│  │  │      │  │  │  payInfoView.html
│  │  │      │  │  │  payInfoView.html.old
│  │  │      │  │  │  point.html
│  │  │      │  │  │  text.html
│  │  │      │  │  │  upload.html
│  │  │      │  │  │  
│  │  │      │  │  ├─chatbot
│  │  │      │  │  │      index.html
│  │  │      │  │  │      mobile.html
│  │  │      │  │  │      
│  │  │      │  │  └─media
│  │  │      │  │          message.html
│  │  │      │  │          
│  │  │      │  ├─include
│  │  │      │  │      tpl.html
│  │  │      │  │      
│  │  │      │  ├─message
│  │  │      │  │      ping.html
│  │  │      │  │      
│  │  │      │  ├─organization
│  │  │      │  │      add.html
│  │  │      │  │      edit.html
│  │  │      │  │      
│  │  │      │  ├─quality
│  │  │      │  │  │  index.html
│  │  │      │  │  │  
│  │  │      │  │  └─include
│  │  │      │  │          left.html
│  │  │      │  │          
│  │  │      │  ├─service
│  │  │      │  │  ├─agent
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─comment
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─current
│  │  │      │  │  │      index.html
│  │  │      │  │  │      transfer.html
│  │  │      │  │  │      
│  │  │      │  │  ├─history
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─include
│  │  │      │  │  │      left.html
│  │  │      │  │  │      
│  │  │      │  │  ├─leavemsg
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─online
│  │  │      │  │  │      chatmsg.html
│  │  │      │  │  │      contacts.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      trace.html
│  │  │      │  │  │      
│  │  │      │  │  ├─processed
│  │  │      │  │  │      index.html
│  │  │      │  │  │      process.html
│  │  │      │  │  │      
│  │  │      │  │  ├─quene
│  │  │      │  │  │      index.html
│  │  │      │  │  │      
│  │  │      │  │  ├─stats
│  │  │      │  │  │      coment.html
│  │  │      │  │  │      consult.html
│  │  │      │  │  │      report.html
│  │  │      │  │  │      
│  │  │      │  │  ├─summary
│  │  │      │  │  │      index.html
│  │  │      │  │  │      process.html
│  │  │      │  │  │      processed.html
│  │  │      │  │  │      
│  │  │      │  │  └─user
│  │  │      │  │          index.html
│  │  │      │  │          
│  │  │      │  ├─setting
│  │  │      │  │  ├─agent
│  │  │      │  │  │      acd.html
│  │  │      │  │  │      adadd.html
│  │  │      │  │  │      adedit.html
│  │  │      │  │  │      adv.html
│  │  │      │  │  │      blacklist.html
│  │  │      │  │  │      index.html
│  │  │      │  │  │      tag.html
│  │  │      │  │  │      tagadd.html
│  │  │      │  │  │      tagedit.html
│  │  │      │  │  │      
│  │  │      │  │  ├─include
│  │  │      │  │  │      left.html
│  │  │      │  │  │      
│  │  │      │  │  └─quickreply
│  │  │      │  │          add.html
│  │  │      │  │          addtype.html
│  │  │      │  │          edit.html
│  │  │      │  │          edittype.html
│  │  │      │  │          imp.html
│  │  │      │  │          index.html
│  │  │      │  │          replylist.html
│  │  │      │  │          
│  │  │      │  └─tenant
│  │  │      │          add.html
│  │  │      │          edit.html
│  │  │      │          index.html
│  │  │      │          
│  │  │      ├─public
│  │  │      │      agent.html
│  │  │      │      agentstatus.html
│  │  │      │      contacts.html
│  │  │      │      macro.html
│  │  │      │      organ.html
│  │  │      │      quickreply.html
│  │  │      │      secfield.html
│  │  │      │      select.html
│  │  │      │      success.html
│  │  │      │      upload.html
│  │  │      │      users.html
│  │  │      │      
│  │  │      └─resource
│  │  │          └─css
│  │  │                  system.html
│  │  │                  ukefu.html
│  │  │                  
│  │  └─webapp
```
