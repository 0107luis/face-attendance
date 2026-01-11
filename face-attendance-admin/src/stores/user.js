import { defineStore } from 'pinia'
import { login, getUserInfo } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  actions: {
    async login(username, password) {
      const res = await login({ username, password })
      this.token = res.data.token
      localStorage.setItem('token', res.data.token)
      return res.data
    },
    async getUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
      return res.data
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    }
  }
})
