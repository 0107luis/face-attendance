import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request

export const login = (data) => request.post('/auth/login', data)
export const getUserInfo = () => request.get('/auth/info')

export const getUserList = (params) => request.get('/user/list', { params })
export const addUser = (data) => request.post('/user', data)
export const updateUser = (data) => request.put('/user', data)
export const deleteUser = (id) => request.delete(`/user/${id}`)

export const getAttendanceList = (params) => request.get('/attendance/page', { params })
export const getAttendanceStats = (params) => request.get('/attendance/stats', { params })

export const getAppealList = (params) => request.get('/appeal/page', { params })
export const approveAppeal = (id, data) => request.post(`/appeal/approve/${id}`, data)
export const rejectAppeal = (id, data) => request.post(`/appeal/reject/${id}`, data)

export const getAntiCheatList = (params) => request.get('/anticheat/list', { params })

export const getDeptList = () => request.get('/dept/list')
