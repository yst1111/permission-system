import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: MainLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: {
          title: '仪表板',
          requiresAuth: true
        }
      },
      {
        path: 'reading-notes',
        name: 'ReadingNotes',
        component: () => import('@/views/ReadingNotes.vue'),
        meta: {
          title: '读书笔记',
          requiresAuth: true
        }
      },
      {
        path: 'diary',
        name: 'Diary',
        component: () => import('@/views/Diary.vue'),
        meta: {
          title: '我的日记',
          requiresAuth: true
        }
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: {
          title: '系统管理',
          requiresAuth: true
        },
        children: [
          {
            path: 'user',
            name: 'User',
            component: () => import('@/views/system/User.vue'),
            meta: {
              title: '用户管理',
              requiresAuth: true
            }
          },
          {
            path: 'role',
            name: 'Role',
            component: () => import('@/views/system/Role.vue'),
            meta: {
              title: '角色管理',
              requiresAuth: true
            }
          },
          {
            path: 'menu',
            name: 'Menu',
            component: () => import('@/views/system/Menu.vue'),
            meta: {
              title: '菜单管理',
              requiresAuth: true
            }
          },
          {
            path: 'meal-card',
            name: 'MealCard',
            component: () => import('@/views/system/MealCard.vue'),
            meta: {
              title: '饭卡管理',
              requiresAuth: true
            }
          }
        ]
      },
      {
        path: 'region',
        name: 'Region',
        component: () => import('@/views/Region.vue'),
        meta: {
          title: '地区管理',
          requiresAuth: true
        }
      },
      {
        path: 'region/import',
        name: 'RegionImport',
        component: () => import('@/views/RegionImport.vue'),
        meta: {
          title: '地区数据导入',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 权限管理系统`
  }
  
  if (requiresAuth && !token) {
    // 需要认证但没有token，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录用户访问登录页，跳转到仪表板
    next('/dashboard')
  } else {
    next()
  }
})

export default router 