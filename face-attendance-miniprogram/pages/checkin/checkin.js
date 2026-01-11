const api = require('../../utils/api.js')

Page({
  data: {
    hasCheckedIn: false,
    hasCheckedOut: false,
    checkInTime: '',
    checkOutTime: ''
  },

  onShow() {
    this.checkTodayStatus()
  },

  checkTodayStatus() {
    const userId = wx.getStorageSync('userId')
    if (!userId) return

    api.getTodayAttendance(userId).then(res => {
      const record = res.data
      if (record && record.checkInTime) {
        this.setData({
          hasCheckedIn: true,
          checkInTime: record.checkInTime
        })
      }
      if (record && record.checkOutTime) {
        this.setData({
          hasCheckedOut: true,
          checkOutTime: record.checkOutTime
        })
      }
    }).catch(err => {
      console.error(err)
    })
  },

  handleCheckIn() {
    wx.showLoading({ title: '正在签到...' })
    
    const userId = wx.getStorageSync('userId')
    const deviceInfo = wx.getSystemInfoSync().model
    
    api.checkIn({
      userId,
      photo: '',
      liveness: 0.95,
      location: '公司总部',
      deviceInfo
    }).then(res => {
      wx.hideLoading()
      this.setData({
        hasCheckedIn: true,
        checkInTime: res.data.checkInTime
      })
      wx.showToast({ title: '签到成功', icon: 'success' })
    }).catch(err => {
      wx.hideLoading()
      wx.showToast({ title: err.message || '签到失败', icon: 'none' })
    })
  },

  handleCheckOut() {
    wx.showLoading({ title: '正在签退...' })
    
    const userId = wx.getStorageSync('userId')
    const deviceInfo = wx.getSystemInfoSync().model
    
    api.checkOut({
      userId,
      photo: '',
      liveness: 0.95,
      location: '公司总部',
      deviceInfo
    }).then(res => {
      wx.hideLoading()
      this.setData({
        hasCheckedOut: true,
        checkOutTime: res.data.checkOutTime
      })
      wx.showToast({ title: '签退成功', icon: 'success' })
    }).catch(err => {
      wx.hideLoading()
      wx.showToast({ title: err.message || '签退失败', icon: 'none' })
    })
  }
})
