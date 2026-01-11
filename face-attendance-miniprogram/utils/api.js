const app = getApp()
const API_BASE = 'http://localhost:8085/api'

const request = (url, method = 'GET', data = {}) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.request({
      url: API_BASE + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data)
        } else {
          wx.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          })
          reject(res.data)
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

module.exports = {
  API_BASE,
  login: (username, password) => request('auth/login', 'POST', { username, password }),
  register: (data) => request('auth/register', 'POST', data),
  getUserInfo: () => request('auth/info', 'GET'),
  checkFaceStatus: (userId) => request(`face/check/${userId}`, 'GET'),
  saveFaceFeature: (data) => request('face/save', 'POST', data),
  checkIn: (data) => request('attendance/checkIn', 'POST', data),
  checkOut: (data) => request('attendance/checkOut', 'POST', data),
  getTodayAttendance: (userId) => request(`attendance/today/${userId}`, 'GET'),
  getAttendanceList: (userId, startDate, endDate) => 
    request(`attendance/list/${userId}`, 'GET', { startDate, endDate }),
  getStatistics: (userId, startDate, endDate) => 
    request(`attendance/statistics/${userId}`, 'GET', { startDate, endDate })
}
