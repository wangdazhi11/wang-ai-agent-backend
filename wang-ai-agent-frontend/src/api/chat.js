import axios from 'axios'

export function ping() {
  return axios.get('/api/health').catch(() => null)
}