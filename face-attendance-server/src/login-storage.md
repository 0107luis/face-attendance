# 浏览器登录/注册存储说明

## 1. 登录成功后存储内容

登录成功时（`UserAPI.login` 调用成功），前端会执行：

```js
localStorage.setItem('currentUser', JSON.stringify(data));
```

这里的 `data` 是后端返回的 `LoginResponseDTO` 对象，序列化为 JSON 字符串保存到浏览器 `localStorage` 中。

- **存储位置**：`localStorage`
- **键名**：`"currentUser"`
- **值结构（主要字段）**：
  - `userId`：用户ID
  - `studentId`：学生ID（仅学生有，用于选课/考勤接口的 studentId）
  - `username`：用户名
  - `realName`：真实姓名
  - `phoneNumber`：手机号
  - `email`：邮箱
  - `roleId`：角色ID（1=系统管理员，2=教师，3=学生）
  - `roleName`：角色名称（系统管理员 / 教师 / 学生）
  - `isActive`：账号是否启用
  - `studentNumber`：学号（仅学生有）
  - `jobNumber`：工号（仅教师有）
  - `className`：班级（学生）
  - `department`：所属院系（教师）
  - `jobTitle`：职称（教师）

### 关于 `X-User-Id` 请求头
- 前端所有需要身份校验的 API 请求，会从 `currentUser.userId` 取值，放入请求头 `X-User-Id`。
- 代码位置：`static/js/api.js` 的 `API.request` 会自动附加：
  ```js
  if (userId) {
      defaultOptions.headers['X-User-Id'] = userId.toString();
  }
  ```
- 后端用该请求头识别当前用户并做权限校验（如访问学生/教师/班级信息、选课、考勤等接口）。

> 注：前端通过 `getCurrentUser()` 读取该对象，并根据 `roleId` 和其他字段做权限控制、界面展示。

```js
function getCurrentUser() {
    var userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
}
```

## 2. 登录状态的使用位置

- **登录校验**：
  - 进入 `index.html` 时调用 `checkLogin()`，如果没有 `currentUser`，会跳转到 `login.html`。
- **顶部用户信息展示**：
  - 使用 `currentUser.realName`、`currentUser.roleId` 在右上角显示姓名和角色。
- **菜单权限控制**：
  - `applyMenuPermissions()` 读取 `currentUser.roleId`，按角色隐藏/显示不同菜单项（管理员 / 教师 / 学生）。
- **学生“我的信息”**：
  - 使用 `currentUser.studentNumber` 请求当前学生在后端的详细信息，只展示自己的记录。
- **教师“我的信息”**：
  - 使用 `currentUser.jobNumber`、`department`、`jobTitle` 等字段展示教师个人资料。
- **业务功能**：
  - 学生选课、教师发布考勤等功能使用 `currentUser.userId` 或学号/工号进行权限校验和数据查询。

## 3. 注册时的存储行为

注册（`UserAPI.register`）成功后：

- 只在页面上提示注册成功信息（角色、学号/工号）。
- **不会** 自动往 `localStorage` 写入 `currentUser`。
- 用户需要使用注册时的用户名和密码再次登录，登录成功后才会写入 `currentUser`。

也就是说：

- **注册阶段**：浏览器不持久保存登录态，仅展示一次性提示。
- **登录阶段**：才会创建/更新 `localStorage.currentUser`。

## 4. 退出登录时的清理

用户点击右上角姓名退出时，前端会执行：

```js
function logout() {
    localStorage.removeItem('currentUser');
    window.location.href = '/login.html';
}
```

- 删除 `localStorage` 中的 `currentUser` 键
- 立即跳转回登录页面

这意味着：
- 退出后刷新 `index.html` 会再次被 `checkLogin()` 重定向到 `login.html`。
- 浏览器中不再保留任何登录态数据，需要重新登录。
