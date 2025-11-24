Page({
  data: {
    username: '',
    password: '',
    isLogged: false,
    realName: '',
    department: ''
  },

  onShow() {
    const token = wx.getStorageSync('token')
    const realName = wx.getStorageSync('realName')
    const department = wx.getStorageSync('department')
    this.setData({
      isLogged: !!token,
      realName: realName || '',
      department: department || ''
    })
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  handleLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' })
      return
    }

    const api = require('../../utils/api.js')
    api.login(username, password).then(res => {
      wx.setStorageSync('token', res.data.token)
      wx.setStorageSync('userId', res.data.userId)
      wx.setStorageSync('realName', res.data.realName)
      wx.setStorageSync('department', res.data.department)
      this.setData({
        isLogged: true,
        realName: res.data.realName,
        department: res.data.department
      })
      wx.showToast({ title: '登录成功', icon: 'success' })
    }).catch(err => {
      console.error(err)
    })
  },

  navigateToCheckin() {
    wx.navigateTo({ url: '/pages/checkin/checkin' })
  },

  navigateToRecord() {
    wx.navigateTo({ url: '/pages/record/record' })
  }
})
