// ========== 模态框和 Toast 管理 ==========

// Toast 提示
function showToast(message, type = 'success', duration = 3000) {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icons = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
    };
    
    const titles = {
        success: '成功',
        error: '错误',
        warning: '警告',
        info: '提示'
    };
    
    toast.innerHTML = `
        <div class="toast-icon">${icons[type]}</div>
        <div class="toast-content">
            <div class="toast-title">${titles[type]}</div>
            <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close">×</button>
    `;
    
    container.appendChild(toast);
    
    // 关闭按钮事件
    toast.querySelector('.toast-close').addEventListener('click', () => {
        toast.style.animation = 'slideInRight 0.3s ease reverse';
        setTimeout(() => toast.remove(), 300);
    });
    
    // 自动关闭
    if (duration > 0) {
        setTimeout(() => {
            toast.style.animation = 'slideInRight 0.3s ease reverse';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    }
}

// 模态框管理
const Modal = {
    overlay: null,
    title: null,
    body: null,
    footer: null,
    submitBtn: null,
    cancelBtn: null,
    closeBtn: null,
    currentCallback: null,
    
    init() {
        this.overlay = document.getElementById('modal-overlay');
        this.title = document.getElementById('modal-title');
        this.body = document.getElementById('modal-body');
        this.footer = document.getElementById('modal-footer');
        this.submitBtn = document.getElementById('modal-submit');
        this.cancelBtn = document.getElementById('modal-cancel');
        this.closeBtn = document.getElementById('modal-close');
        
        // 绑定关闭事件
        this.closeBtn.addEventListener('click', () => this.close());
        this.cancelBtn.addEventListener('click', () => this.close());
        this.overlay.addEventListener('click', (e) => {
            if (e.target === this.overlay) this.close();
        });
        
        // ESC 键关闭
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.overlay.classList.contains('active')) {
                this.close();
            }
        });
    },
    
    open(options) {
        this.title.textContent = options.title || '提示';
        this.body.innerHTML = options.content || '';
        
        // 显示/隐藏底部按钮
        if (options.showFooter === false) {
            this.footer.style.display = 'none';
        } else {
            this.footer.style.display = 'flex';
        }
        
        // 设置按钮文本
        this.submitBtn.textContent = options.submitText || '确定';
        this.cancelBtn.textContent = options.cancelText || '取消';
        
        // 设置按钮样式
        this.submitBtn.className = `btn ${options.submitClass || 'btn-accent'}`;
        
        // 保存回调
        this.currentCallback = options.onSubmit;
        
        // 绑定提交事件
        const submitHandler = () => {
            if (this.currentCallback) {
                const result = this.currentCallback();
                // 如果返回 false，不关闭模态框
                if (result !== false) {
                    this.close();
                }
            } else {
                this.close();
            }
        };
        
        // 移除旧的事件监听器
        const newSubmitBtn = this.submitBtn.cloneNode(true);
        this.submitBtn.parentNode.replaceChild(newSubmitBtn, this.submitBtn);
        this.submitBtn = newSubmitBtn;
        this.submitBtn.addEventListener('click', submitHandler);
        
        // 显示模态框
        this.overlay.classList.add('active');
        
        // 自动聚焦第一个输入框
        setTimeout(() => {
            const firstInput = this.body.querySelector('input, select, textarea');
            if (firstInput) firstInput.focus();
        }, 100);
    },
    
    close() {
        this.overlay.classList.remove('active');
        this.currentCallback = null;
    },
    
    // 表单模态框
    form(options) {
        const fields = options.fields || [];
        const formHTML = fields.map(field => {
            const required = field.required ? '<span class="required">*</span>' : '';
            const value = field.value || '';
            
            let inputHTML = '';
            if (field.type === 'select') {
                const optionsHTML = field.options.map(opt => 
                    `<option value="${opt.value}" ${opt.value === value ? 'selected' : ''}>${opt.label}</option>`
                ).join('');
                inputHTML = `<select id="${field.id}" name="${field.name}">${optionsHTML}</select>`;
            } else if (field.type === 'textarea') {
                inputHTML = `<textarea id="${field.id}" name="${field.name}" ${field.required ? 'required' : ''}>${value}</textarea>`;
            } else {
                inputHTML = `<input type="${field.type || 'text'}" id="${field.id}" name="${field.name}" value="${value}" ${field.required ? 'required' : ''}>`;
            }
            
            return `
                <div class="form-group">
                    <label for="${field.id}">${field.label}${required}</label>
                    ${inputHTML}
                    <div class="error-message" id="${field.id}-error"></div>
                </div>
            `;
        }).join('');
        
        this.open({
            title: options.title,
            content: formHTML,
            submitText: options.submitText || '提交',
            submitClass: options.submitClass || 'btn-accent',
            onSubmit: () => {
                // 收集表单数据
                const formData = {};
                let isValid = true;
                
                fields.forEach(field => {
                    const input = document.getElementById(field.id);
                    const value = input.value.trim();
                    
                    // 验证必填项
                    if (field.required && !value) {
                        isValid = false;
                        input.parentElement.classList.add('error');
                        const errorEl = document.getElementById(`${field.id}-error`);
                        errorEl.textContent = `${field.label}不能为空`;
                        errorEl.classList.add('show');
                    } else {
                        input.parentElement.classList.remove('error');
                        const errorEl = document.getElementById(`${field.id}-error`);
                        errorEl.classList.remove('show');
                    }
                    
                    formData[field.name] = value;
                });
                
                if (!isValid) {
                    return false; // 阻止关闭模态框
                }
                
                // 调用回调
                if (options.onSubmit) {
                    options.onSubmit(formData);
                }
            }
        });
    },
    
    // 确认对话框
    confirm(options) {
        this.open({
            title: options.title || '确认',
            content: `<p style="font-size: 1.1rem; line-height: 1.6;">${options.message}</p>`,
            submitText: options.submitText || '确定',
            cancelText: options.cancelText || '取消',
            submitClass: options.danger ? 'btn-danger' : 'btn-accent',
            onSubmit: options.onConfirm
        });
    }
};

// ========== 登录状态检查 ==========
function checkLogin() {
    var currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
        window.location.href = '/login.html';
        return null;
    }
    return JSON.parse(currentUser);
}

// 获取当前用户信息
function getCurrentUser() {
    var userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
}

// 获取当前学生ID（优先从localStorage，其次通过学号查找并缓存）
async function getCurrentStudentId() {
    var currentUser = getCurrentUser();
    if (!currentUser) return null;
    if (currentUser.studentId) {
        return currentUser.studentId;
    }
    if (currentUser.studentNumber) {
        try {
            const student = await StudentAPI.getByNumber(currentUser.studentNumber);
            if (student && student.studentId) {
                currentUser.studentId = student.studentId;
                localStorage.setItem('currentUser', JSON.stringify(currentUser));
                return student.studentId;
            }
        } catch (e) {
            console.error('获取学生ID失败:', e);
        }
    }
    return null;
}

// 退出登录
function logout() {
    localStorage.removeItem('currentUser');
    window.location.href = '/login.html';
}

// 更新顶部用户信息显示
function updateUserDisplay() {
    var user = getCurrentUser();
    if (user) {
        var userAvatar = document.querySelector('.user-avatar');
        var userName = document.querySelector('.user-name');
        if (userAvatar && user.realName) {
            userAvatar.textContent = user.realName.charAt(0);
        }
        if (userName) {
            var roleNames = {1: '管理员', 2: '教师', 3: '学生'};
            var roleName = roleNames[user.roleId] || '';
            userName.textContent = user.realName + (roleName ? ' (' + roleName + ')' : '');
            userName.style.cursor = 'pointer';
            userName.title = '点击退出登录';
            userName.onclick = function() {
                if (confirm('确定要退出登录吗？')) {
                    logout();
                }
            };
        }
    }
}

// ========== 角色权限控制 ==========
// 菜单权限配置：定义每个角色可以看到的菜单
const FACE_SERVICE_URL = window.FACE_SERVICE_URL || 'http://127.0.0.1:8000/embed';

var menuPermissions = {
    // 管理员(roleId=1): 所有菜单
    1: {
        'permission': true,
        'permission-assign': true,
        'dashboard': true,
        'basic-info': true,
        'user-management': true,
        'role-management': true,
        'teacher-management': true,
        'student-management': true,
        'course-management': true,
        'admin-student-courses': true,
        'business': true,
        'publish-task': true,
        'statistics': true,
        'face-data-entry': true,
        'face-test': true,
        'course-selection': true
    },
    // 教师(roleId=2): 仪表盘、学生信息、自己的教师信息、课程信息、考勤管理
    2: {
        'permission': false,
        'permission-assign': false,
        'dashboard': true,
        'basic-info': true,
        'user-management': false,
        'role-management': false,
        'teacher-management': true,   // 只能看自己的教师信息
        'student-management': true,   // 只能看自己班级的学生
        'course-management': true,    // 只能看自己的课程
        'admin-student-courses': false,
        'business': true,
        'publish-task': true,
        'statistics': true,
        'face-data-entry': true,
        'face-test': true,
        'course-selection': false
    },
    // 学生(roleId=3): 仅能管理自己的学生信息、选课
    3: {
        'permission': false,
        'permission-assign': false,
        'dashboard': true,
        'basic-info': true,
        'user-management': false,
        'role-management': false,
        'teacher-management': false,
        'student-management': true,  // 只能看自己的信息
        'course-management': false,
        'admin-student-courses': false,
        'business': true,
        'publish-task': false,
        'statistics': true,      // 可以查看考勤记录
        'face-data-entry': true,
        'face-test': true,
        'course-selection': true     // 可以选课
    }
};

// 根据用户角色过滤菜单
function applyMenuPermissions() {
    var user = getCurrentUser();
    if (!user) {
        console.log('applyMenuPermissions: 用户未登录');
        return;
    }
    
    var roleId = user.roleId;
    console.log('applyMenuPermissions: 用户角色ID =', roleId, '用户信息 =', user);
    var permissions = menuPermissions[roleId] || {};
    console.log('applyMenuPermissions: 权限配置 =', permissions);
    
    // 处理主菜单项
    var menuItems = document.querySelectorAll('.menu-item');
    menuItems.forEach(function(item) {
        var target = item.getAttribute('data-target');
        if (permissions[target] === false) {
            item.style.display = 'none';
            // 同时隐藏对应的子菜单
            var nextEl = item.nextElementSibling;
            if (nextEl && nextEl.classList.contains('submenu')) {
                nextEl.style.display = 'none';
            }
        }
    });
    
    // 处理子菜单项
    var submenuItems = document.querySelectorAll('.submenu-item');
    submenuItems.forEach(function(item) {
        var target = item.getAttribute('data-target');
        if (permissions[target] === false) {
            item.style.display = 'none';
        }
    });
    
    // 检查父菜单下是否所有子菜单都隐藏了，如果是则隐藏父菜单
    var submenus = document.querySelectorAll('.submenu');
    submenus.forEach(function(submenu) {
        var visibleItems = submenu.querySelectorAll('.submenu-item:not([style*="display: none"])');
        if (visibleItems.length === 0) {
            submenu.style.display = 'none';
            var prevEl = submenu.previousElementSibling;
            if (prevEl && prevEl.classList.contains('menu-item')) {
                prevEl.style.display = 'none';
            }
        }
    });
}

// 加载仪表板数据
async function loadDashboardData() {
    try {
        // 加载统计数据
        const stats = await DashboardAPI.getStatistics();
        document.getElementById('stat-student-count').textContent = stats.studentCount || 0;
        document.getElementById('stat-teacher-count').textContent = stats.teacherCount || 0;
        document.getElementById('stat-course-count').textContent = stats.courseCount || 0;
        document.getElementById('stat-attendance-rate').textContent = stats.todayAttendanceRate || '0.0%';
    } catch (error) {
        console.error('加载统计数据失败:', error);
        document.getElementById('stat-student-count').textContent = '-';
        document.getElementById('stat-teacher-count').textContent = '-';
        document.getElementById('stat-course-count').textContent = '-';
        document.getElementById('stat-attendance-rate').textContent = '-';
    }

    try {
        // 加载最近考勤记录
        const records = await DashboardAPI.getRecentRecords();
        const tbody = document.getElementById('recent-records-tbody');
        
        if (records && records.length > 0) {
            tbody.innerHTML = records.map(record => `
                <tr>
                    <td>${record.courseName || '-'}</td>
                    <td>${record.className || '-'}</td>
                    <td>${record.time || '-'}</td>
                    <td>${record.total || 0}/${record.attended || 0}</td>
                    <td>${record.rate || '0.0%'}</td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">暂无数据</td></tr>';
        }
    } catch (error) {
        console.error('加载考勤记录失败:', error);
        document.getElementById('recent-records-tbody').innerHTML = 
            '<tr><td colspan="5" style="text-align: center;">加载失败</td></tr>';
    }
}

// 页面切换逻辑
document.addEventListener('DOMContentLoaded', function () {
    // 检查登录状态
    var currentUser = checkLogin();
    if (!currentUser) return;
    
    // 更新用户显示
    updateUserDisplay();
    
    // 应用菜单权限
    applyMenuPermissions();
    
    // 初始化模态框
    Modal.init();
    
    // 加载仪表板数据
    loadDashboardData();
    
    // 预加载高德地图API，提升后续地图初始化速度
    if (typeof initAMap === 'function') {
        initAMap(null, null).then((AMap) => {
            window.AMapInstance = AMap;
            console.log('高德地图API预加载完成');
        }).catch((e) => {
            console.warn('高德地图API预加载失败:', e);
        });
    }
    const menuItems = document.querySelectorAll('.menu-item');
    const submenuItems = document.querySelectorAll('.submenu-item');

    function hideAllPages() {
        document.querySelectorAll('.page.active').forEach(function (page) {
            page.classList.remove('active');
        });
    }

    function showPage(id, title) {
        let targetPage = document.getElementById(id);
        if (!targetPage) {
            targetPage = createPage(id, title);
        }
        hideAllPages();
        targetPage.classList.add('active');
        document.querySelector('.page-title').textContent = title;
    }

    // 菜单点击事件（统一处理：容器菜单只展开，不切页）
    menuItems.forEach(function (item) {
        item.addEventListener('click', function () {
            const target = this.getAttribute('data-target');
            const textEl = this.querySelector('.menu-text');
            const title = textEl ? textEl.textContent : '';

            // 如果是容器菜单（有子菜单），仅展开/折叠子菜单并返回
            if (this.matches('[data-target="basic-info"], [data-target="business"], [data-target="permission"]')) {
                // 切换菜单激活状态
                menuItems.forEach(function (mi) { mi.classList.remove('active'); });
                this.classList.add('active');

                const submenu = this.nextElementSibling;
                if (submenu && submenu.classList.contains('submenu')) {
                    submenu.classList.toggle('show');
                }
                return;
            }

            // 普通菜单项：切换页面
            menuItems.forEach(function (mi) { mi.classList.remove('active'); });
            this.classList.add('active');
            // 清除子菜单选中状态
            submenuItems.forEach(function (smi) { smi.classList.remove('active'); });
            showPage(target, title);
        });
    });

    // 子菜单点击事件
    submenuItems.forEach(function (item) {
        item.addEventListener('click', function () {
            const target = this.getAttribute('data-target');
            const title = this.textContent.trim();

            // 移除所有子菜单项的激活状态
            submenuItems.forEach(function (smi) { smi.classList.remove('active'); });
            this.classList.add('active');

            showPage(target, title);
        });
    });

    // 创建页面的函数（仅创建并返回，不负责显示/隐藏）
    function createPage(id, title) {
        // 创建新页面
        const newPage = document.createElement('div');
        newPage.id = id;
        newPage.className = 'page';

        // 根据ID设置页面内容
        let content = '';
        switch (id) {
            case 'course-management':
                var currentUser = getCurrentUser();
                var isTeacherRole = currentUser && currentUser.roleId === 2;
                var isStudentRole = currentUser && currentUser.roleId === 3;
                
                if (isTeacherRole) {
                    // 教师只能看到自己的课程
                    content = `
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">我的课程</div>
                                <button class="btn" onclick="loadMyCourses()">刷新</button>
                            </div>
                            <div class="card-body">
                                <div id="my-courses-container">
                                    <p style="text-align: center; color: #888;">加载中...</p>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadMyCourses, 100);
                } else if (isStudentRole) {
                    // 学生只能查看所有课程，不能操作
                    content = `
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">课程列表</div>
                                <button class="btn" onclick="loadCourses()">刷新</button>
                            </div>
                            <div class="card-body">
                                <div class="table-container">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>课程编号</th>
                                                <th>课程名称</th>
                                                <th>学分</th>
                                                <th>学期</th>
                                            </tr>
                                        </thead>
                                        <tbody id="course-table-body">
                                            <tr>
                                                <td colspan="3" style="text-align: center;">加载中...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadCourses, 100);
                } else {
                    // 管理员可以管理所有课程
                    content = `
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">课程信息管理</div>
                                <div class="card-actions">
                                    <button class="btn btn-accent" onclick="addCourse()">添加课程</button>
                                    <select id="course-page-size" onchange="coursePageState && (coursePageState.page=0, coursePageState.size=parseInt(this.value,10)||10); loadCourses();" style="width: 100px; margin-right: 8px;">
                                        <option value="10">10</option>
                                        <option value="15">15</option>
                                        <option value="20">20</option>
                                        <option value="50">50</option>
                                    </select>
                                    <button class="btn" onclick="loadCourses()">刷新</button>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="table-container">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>课程编号</th>
                                                <th>课程名称</th>
                                                <th>学分</th>
                                                <th>学期</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody id="course-table-body">
                                            <tr>
                                                <td colspan="5" style="text-align: center;">加载中...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="display:flex;justify-content:flex-end;align-items:center;margin-top:10px;gap:8px;">
                                    <button class="btn" id="course-page-prev" onclick="coursePageState.page=Math.max(0,coursePageState.page-1);loadCourses();">上一页</button>
                                    <span>第 <span id="course-page-current">1</span> / <span id="course-page-total">1</span> 页</span>
                                    <button class="btn" id="course-page-next" onclick="coursePageState.page=Math.min(coursePageState.totalPages-1,coursePageState.page+1);loadCourses();">下一页</button>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadCourses, 100);
                }
                break;
            case 'teacher-management':
                var currentUser = getCurrentUser();
                var isTeacher = currentUser && currentUser.roleId === 2;
                
                if (isTeacher) {
                    // 教师只能看到自己的信息
                    content = `
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">我的信息</div>
                                <button class="btn" onclick="loadMyTeacherInfo()">刷新</button>
                            </div>
                            <div class="card-body">
                                <div id="my-teacher-info" style="padding: 20px;">
                                    <p style="text-align: center; color: #888;">加载中...</p>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadMyTeacherInfo, 100);
                } else {
                    // 管理员可以看到所有教师
                    content = `
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">教师信息管理</div>
                                <div class="card-actions">
                                    <button class="btn btn-accent" onclick="addTeacher()">添加教师</button>
                                    <select id="teacher-page-size" onchange="teacherPageState && (teacherPageState.page=0, teacherPageState.size=parseInt(this.value,10)||10); loadTeachers();" style="width: 100px; margin-right: 8px;">
                                        <option value="10">10</option>
                   	                 <option value="15">15</option>
                   	                 <option value="20">20</option>
                   	                 <option value="50">50</option>
                                    </select>
                                    <button class="btn" onclick="loadTeachers()">刷新</button>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="table-container">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>用户名</th>
                                                <th>真实姓名</th>
                                                <th>手机号</th>
                                                <th>邮箱</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody id="teacher-table-body">
                                            <tr>
                                                <td colspan="5" style="text-align: center;">加载中...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="display:flex;justify-content:flex-end;align-items:center;margin-top:10px;gap:8px;">
                                    <button class="btn" id="teacher-page-prev" onclick="teacherPageState.page=Math.max(0,teacherPageState.page-1);loadTeachers();">上一页</button>
                                    <span>第 <span id="teacher-page-current">1</span> / <span id="teacher-page-total">1</span> 页</span>
                                    <button class="btn" id="teacher-page-next" onclick="teacherPageState.page=Math.min(teacherPageState.totalPages-1,teacherPageState.page+1);loadTeachers();">下一页</button>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadTeachers, 100);
                }
                break;
            case 'student-management':
                var currentUser = getCurrentUser();
                var isStudent = currentUser && currentUser.roleId === 3;
                
                if (isStudent) {
                    // 学生只能看到自己的信息
                    content = `
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">我的信息</div>
                                <button class="btn" onclick="loadMyStudentInfo()">刷新</button>
                            </div>
                            <div class="card-body">
                                <div id="my-student-info" style="padding: 20px;">
                                    <p style="text-align: center; color: #888;">加载中...</p>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadMyStudentInfo, 100);
                } else {
                    // 管理员和教师可以看到学生列表
                    content = `
                        <div class="card">
                            <div class="card-header">
                                <div class="card-title">学生信息管理</div>
                                <div class="card-actions">
                                    <button class="btn btn-accent" onclick="addStudent()">添加学生</button>
                                    <select id="student-page-size" onchange="studentPageState && (studentPageState.page=0, studentPageState.size=parseInt(this.value,10)||10); loadStudents();" style="width: 100px; margin-right: 8px;">
                                        <option value="10">10</option>
                                        <option value="15">15</option>
                                        <option value="20">20</option>
                                        <option value="50">50</option>
                                    </select>
                                    <button class="btn" onclick="loadStudents()">刷新</button>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="table-container">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>学号</th>
                                                <th>姓名</th>
                                                <th>班级</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody id="student-table-body">
                                            <tr>
                                                <td colspan="4" style="text-align: center;">加载中...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="display:flex;justify-content:flex-end;align-items:center;margin-top:10px;gap:8px;">
                                    <button class="btn" id="student-page-prev" onclick="studentPageState.page=Math.max(0,studentPageState.page-1);loadStudents();">上一页</button>
                                    <span>第 <span id="student-page-current">1</span> / <span id="student-page-total">1</span> 页</span>
                                    <button class="btn" id="student-page-next" onclick="studentPageState.page=Math.min(studentPageState.totalPages-1,studentPageState.page+1);loadStudents();">下一页</button>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadStudents, 100);
                }
                break;
            case 'user-management':
                content = `
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">用户信息管理</div>
                            <div class="card-actions">
                                <button class="btn btn-accent" onclick="addUser()">添加用户</button>
                                <select id="user-page-size" onchange="userPageState && (userPageState.page=0); loadUsersTable();" style="width: 120px; margin-right: 8px;">
                                    <option value="10">10</option>
                                    <option value="15">15</option>
                                    <option value="20">20</option>
                                    <option value="50">50</option>
                                </select>
                                <button class="btn" onclick="loadUsersTable()">刷新</button>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>用户ID</th>
                                            <th>用户名</th>
                                            <th>真实姓名</th>
                                            <th>角色</th>
                                            <th>手机号</th>
                                            <th>邮箱</th>
                                            <th>创建时间</th>
                                            <th>操作</th>
                                        </tr>
                                    </thead>
                                    <tbody id="user-table-body">
                                        <tr>
                                            <td colspan="8" style="text-align: center;">加载中...</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div style="display:flex;justify-content:flex-end;align-items:center;margin-top:10px;gap:8px;">
                                <button class="btn" id="user-page-prev" onclick="userPageState.page=Math.max(0,userPageState.page-1);loadUsersTable();">上一页</button>
                                <span>第 <span id="user-page-current">1</span> / <span id="user-page-total">1</span> 页</span>
                                <button class="btn" id="user-page-next" onclick="userPageState.page=Math.min(userPageState.totalPages-1,userPageState.page+1);loadUsersTable();">下一页</button>
                            </div>
                        </div>
                    </div>
                `;
                setTimeout(loadUsersTable, 100);
                break;
            case 'role-management':
                content = `
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">角色信息管理</div>
                            <div class="card-actions">
                                <button class="btn btn-accent" onclick="addRole()">添加角色</button>
                                <select id="role-page-size" onchange="rolePageState && (rolePageState.page=0, rolePageState.size=parseInt(this.value,10)||10); loadRolesTable();" style="width: 120px; margin-right: 8px;">
                                    <option value="10">10</option>
                                    <option value="15">15</option>
                                    <option value="20">20</option>
                                    <option value="50">50</option>
                                </select>
                                <button class="btn" onclick="loadRolesTable()">刷新</button>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>角色ID</th>
                                            <th>角色名称</th>
                                            <th>角色描述</th>
                                            <th>创建时间</th>
                                            <th>操作</th>
                                        </tr>
                                    </thead>
                                    <tbody id="role-table-body">
                                        <tr>
                                            <td colspan="5" style="text-align: center;">加载中...</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div style="display:flex;justify-content:flex-end;align-items:center;margin-top:10px;gap:8px;">
                                <button class="btn" id="role-page-prev" onclick="rolePageState.page=Math.max(0,rolePageState.page-1);loadRolesTable();">上一页</button>
                                <span>第 <span id="role-page-current">1</span> / <span id="role-page-total">1</span> 页</span>
                                <button class="btn" id="role-page-next" onclick="rolePageState.page=Math.min(rolePageState.totalPages-1,rolePageState.page+1);loadRolesTable();">下一页</button>
                            </div>
                        </div>
                    </div>
                `;
                setTimeout(loadRolesTable, 100);
                break;
            case 'admin-student-courses':
                content = `
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">学生选课查看</div>
                        </div>
                        <div class="card-body">
                            <div class="form-row" style="gap: 12px; margin-bottom: 12px;">
                                <div class="form-group">
                                    <label>学号</label>
                                    <input id="admin-course-query-number" type="text" placeholder="输入学号" />
                                </div>
                                <div class="form-group">
                                    <label>学生ID</label>
                                    <input id="admin-course-query-studentid" type="number" placeholder="可选，直接用学生ID" />
                                </div>
                                <div class="form-group" style="align-self: flex-end;">
                                    <button class="btn btn-accent" id="admin-course-query-btn">查询</button>
                                </div>
                                <div class="form-group" style="align-self: flex-end;">
                                     <button class="btn" id="admin-course-query-all-btn">加载全部</button>
                                </div>
                            </div>
                            <div class="form-row" style="gap: 12px; margin-bottom: 12px;">
                                <div class="form-group">
                                    <label>每页条数</label>
                                    <select id="admin-course-page-size" style="width: 100px;">
                                        <option value="10">10</option>
                                        <option value="15">15</option>
                                        <option value="20">20</option>
                                        <option value="50">50</option>
                                    </select>
                                </div>
                            </div>
                            <div id="admin-course-query-result">
                                <div style="text-align:center;color:#888;padding:20px;">请输入学号或学生ID后查询</div>
                            </div>
                            <div style="display:flex;justify-content:flex-end;align-items:center;margin-top:10px;gap:8px;">
                                <button class="btn" id="admin-course-page-prev" onclick="adminCourseViewState.page=Math.max(0,adminCourseViewState.page-1);renderAdminCourseViewTable();">上一页</button>
                                <span>第 <span id="admin-course-page-current">1</span> / <span id="admin-course-page-total">1</span> 页</span>
                                <button class="btn" id="admin-course-page-next" onclick="adminCourseViewState.page=Math.min(adminCourseViewState.totalPages-1,adminCourseViewState.page+1);renderAdminCourseViewTable();">下一页</button>
                            </div>
                        </div>
                    </div>
                `;
                setTimeout(() => {
                    document.getElementById('admin-course-query-btn')?.addEventListener('click', adminQueryStudentCourses);
                    document.getElementById('admin-course-query-all-btn')?.addEventListener('click', loadAdminStudentCoursesAll);
                    document.getElementById('admin-course-page-size')?.addEventListener('change', () => {
                        adminCourseViewState.page = 0;
                        renderAdminCourseViewTable();
                    });
                }, 50);
                break;
            case 'publish-task':
                content = `
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">发布考勤</div>
                        </div>
                        <div class="card-body">
                            <form id="publish-task-form">
                                <div class="form-group">
                                    <label for="task-class-select">选择班级 <span class="required">*</span></label>
                                    <select id="task-class-select" name="courseClassId" required>
                                        <option value="">正在加载班级...</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="task-name">任务名称 <span class="required">*</span></label>
                                    <input type="text" id="task-name" name="taskName" placeholder="例如：第1周 软件工程考勤" required>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="task-start-time">开始时间 <span class="required">*</span></label>
                                        <!-- 使用 time 类型，只显示时间 -->
                                        <input type="time" id="task-start-time" name="startTime" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="task-duration">持续时长(分钟) <span class="required">*</span></label>
                                        <div class="form-row" style="gap: 10px;">
                                            <select id="task-duration-select" style="flex: 1;" onchange="updateDurationInput(this.value)">
                                                <option value="5">5分钟</option>
                                                <option value="10" selected>10分钟</option>
                                                <option value="15">15分钟</option>
                                                <option value="30">30分钟</option>
                                                <option value="45">45分钟</option>
                                                <option value="custom">自定义</option>
                                            </select>
                                            <input type="number" id="task-duration-input" name="duration" value="10" min="1" style="flex: 1; display: none;" placeholder="输入分钟数">
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label>考勤地点设置 <span class="required">*</span></label>
                                    <div class="form-row" style="align-items: flex-end;">
                                        <div class="form-group" style="flex: 2;">
                                            <label for="location-range" style="font-size: 0.9em;">地点描述</label>
                                            <input type="text" id="location-range" name="locationRange" placeholder="例如：一教302" required>
                                        </div>
                                        <div class="form-group" style="flex: 1;">
                                            <button type="button" class="btn btn-secondary" id="btn-search-location" style="width: 100%; margin-bottom: 5px;">搜索</button>
                                        </div>
                                    </div>
                                    <!-- 地图容器 -->
                                    <div id="map-container" style="height: 300px; width: 100%; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; display: block;"></div>
                                    
                                    <div class="form-row">
                                        <div class="form-group" style="flex: 1;">
                                            <label for="radius" style="font-size: 0.9em;">有效半径(米)</label>
                                            <input type="number" id="radius" name="radius" value="30" required>
                                        </div>
                                    </div>
                                    <!-- 隐藏的经纬度输入框，用于表单提交到数据库 -->
                                    <div style="display: none;">
                                        <input type="number" id="latitude" name="latitude" step="0.0000001" required>
                                        <input type="number" id="longitude" name="longitude" step="0.0000001" required>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-accent" style="margin-top: 10px;">发布考勤</button>
                            </form>
                        </div>
                    </div>
                `;
                setTimeout(initPublishTaskPage, 100);
                break;
            case 'statistics':
                var currentUser = getCurrentUser();
                var isStudent = currentUser && currentUser.roleId === 3;
                
                if (isStudent) {
                    // 学生查看自己的考勤记录
                    content = `
                        <div class="card" id="statistics">
                            <div class="card-header">
                                <div class="card-title">我的考勤记录</div>
                                <button class="btn" onclick="loadMyAttendanceRecords()">刷新</button>
                            </div>
                            <div class="card-body">
                                <div class="table-container">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>打卡时间</th>
                                                <th>考勤结果</th>
                                                <th>备注</th>
                                            </tr>
                                        </thead>
                                        <tbody id="my-attendance-body">
                                            <tr>
                                                <td colspan="4" style="text-align: center;">加载中...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(loadMyAttendanceRecords, 100);
                } else {
                    // 教师和管理员查看统计
                    content = `
                        <div class="card" id="statistics">
                            <div class="card-header">
                                <div class="card-title">考勤统计</div>
                            </div>
                            <div class="card-body">
                                <div class="form-group">
                                    <label>选择班级</label>
                                    <select id="statistics-class-select">
                                        <option value="">正在加载...</option>
                                    </select>
                                </div>
                                <button class="btn btn-accent">生成统计报告</button>

                                <div style="margin-top: 30px;">
                                    <!-- 统计结果将在这里动态显示 -->
                                </div>
                            </div>
                        </div>
                        
                        <!-- 考勤状况显示区域 -->
                        <div id="attendance-status-container" style="display: none; margin-top: 20px;">
                            <div class="card">
                                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
                                    <div class="card-title">班级考勤状况</div>
                                    <button class="btn btn-sm btn-secondary" onclick="closeAttendanceStatus()" style="margin: 0;">
                                        关闭
                                    </button>
                                </div>
                                <div class="card-body" id="attendance-status-content">
                                    <!-- 考勤状况内容将在这里动态显示 -->
                                </div>
                            </div>
                        </div>
                    `;
                    setTimeout(initStatisticsPage, 100);
                }
                break;
            case 'face-data-entry':
                content = `
                    <div class="card" id="face-data-entry-card">
                        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; gap: 12px; flex-wrap: wrap;">
                            <div class="card-title">人脸数据录入</div>
                            <span style="color: #7f8c8d; font-size: 0.9rem;">点击拍照时自动申请摄像头权限</span>
                        </div>
                        <div class="card-body">
                            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 16px; align-items: start;">
                                <div>
                                    <div style="position: relative; background: linear-gradient(145deg, #1f2d3d 0%, #30475e 45%, #22313f 100%); border-radius: 12px; overflow: hidden; min-height: 280px; box-shadow: inset 0 1px 0 rgba(255,255,255,0.05);">
                                        <video id="face-video" autoplay playsinline muted style="width: 100%; height: 100%; object-fit: cover;"></video>
                                        <canvas id="face-canvas" style="display: none;"></canvas>
                                        <div id="face-video-mask" style="position: absolute; inset: 0; pointer-events: none; background: radial-gradient(circle at center, rgba(0,0,0,0) 45%, rgba(0,0,0,0.35) 65%);"></div>
                                        <div style="position: absolute; inset: 12px; border: 1px solid rgba(255,255,255,0.08); border-radius: 12px; pointer-events: none;"></div>
                                    </div>
                                    <div style="margin-top: 12px; display: flex; gap: 10px; flex-wrap: wrap;">
                                        <button class="btn btn-accent" id="btn-face-capture">拍照</button>
                                        <button class="btn" id="btn-face-upload-trigger">上传照片</button>
                                        <button class="btn btn-secondary" id="btn-face-stop-camera">关闭摄像头</button>
                                        <input type="file" id="face-file-input" accept="image/*" style="display: none;">
                                    </div>
                                    <div style="margin-top: 10px; display: flex; gap: 8px; flex-wrap: wrap;">
                                        <input type="text" id="face-url-input" placeholder="粘贴图片链接" style="flex: 1; min-width: 220px; padding: 8px 10px; border: 1px solid #dfe4ea; border-radius: 8px;">
                                        <button class="btn btn-secondary" id="btn-face-url-load">从链接加载</button>
                                    </div>
                                    <p style="margin-top: 8px; color: #7f8c8d; font-size: 0.9rem;">确保光线充足、正脸无遮挡。首次拍照会请求摄像头权限。</p>
                                </div>
                                <div>
                                    <div class="card" style="margin: 0;">
                                        <div class="card-header" style="justify-content: space-between;">
                                            <div class="card-title" style="font-size: 1rem;">预览与提交</div>
                                            <span id="face-status-text" style="color: #7f8c8d; font-size: 0.9rem;"></span>
                                        </div>
                                        <div class="card-body">
                                            <div style="border: 1px dashed #dfe4ea; border-radius: 12px; padding: 10px; text-align: center; min-height: 240px; display: flex; align-items: center; justify-content: center; background: #fafafa;">
                                                <img id="face-preview" alt="人脸预览" style="max-width: 100%; max-height: 320px; display: none; border-radius: 10px; box-shadow: 0 6px 18px rgba(0,0,0,0.08);">
                                                <div id="face-preview-placeholder" style="color: #95a5a6;">等待采集或上传照片</div>
                                            </div>
                                            <button class="btn btn-accent" id="btn-face-submit" style="width: 100%; margin-top: 12px;">提交并保存</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                setTimeout(initFaceDataEntryPage, 80);
                break;
            case 'face-test':
                content = `
                    <div class="card" id="face-test-card">
                        <div class="card-header" style="justify-content: space-between; align-items: center;">
                            <div class="card-title">人脸识别测试</div>
                            <span style="color: #7f8c8d; font-size: 0.9rem;">上传人脸照片，调用服务端模型检测与提取特征</span>
                        </div>
                        <div class="card-body">
                            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 16px; align-items: start;">
                                <div>
                                    <div style="border: 1px dashed #dfe4ea; border-radius: 12px; padding: 10px; text-align: center; min-height: 260px; display: flex; align-items: center; justify-content: center; background: #fafafa;">
                                        <img id="face-test-preview" alt="人脸预览" style="max-width: 100%; max-height: 320px; display: none; border-radius: 10px; box-shadow: 0 6px 18px rgba(0,0,0,0.08);">
                                        <div id="face-test-placeholder" style="color: #95a5a6;">请选择或拖拽图片</div>
                                    </div>
                                    <div style="margin-top: 12px; display: flex; gap: 10px; flex-wrap: wrap;">
                                        <input type="file" id="face-test-file" accept="image/*" style="display: none;">
                                        <button class="btn btn-accent" id="btn-face-test-upload">选择图片</button>
                                        <button class="btn" id="btn-face-test-clear">清除</button>
                                    </div>
                                    <div style="margin-top: 10px; display: flex; gap: 8px; flex-wrap: wrap;">
                                        <input type="text" id="face-test-url-input" placeholder="粘贴图片链接" style="flex: 1; min-width: 220px; padding: 8px 10px; border: 1px solid #dfe4ea; border-radius: 8px;">
                                        <button class="btn btn-secondary" id="btn-face-test-url-load">从链接加载</button>
                                    </div>
                                    <p style="margin-top: 8px; color: #7f8c8d; font-size: 0.9rem;">支持 JPG/PNG；图片会直接发送到服务端模型，不会本地存储。</p>
                                </div>
                                <div>
                                    <div class="card" style="margin: 0;">
                                        <div class="card-header" style="justify-content: space-between;">
                                            <div class="card-title" style="font-size: 1rem;">检测结果</div>
                                            <span id="face-test-status" style="color: #7f8c8d; font-size: 0.9rem;">等待上传</span>
                                        </div>
                                        <div class="card-body" style="display: flex; flex-direction: column; gap: 12px;">
                                            <button class="btn btn-accent" id="btn-face-test-run">调用识别服务</button>
                                            <pre id="face-test-result" style="background: #f7f9fb; border-radius: 10px; padding: 12px; min-height: 160px; border: 1px solid #e5e9ef; overflow: auto; white-space: pre-wrap; word-break: break-all;">尚未开始测试</pre>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                setTimeout(initFaceTestPage, 50);
                break;
            case 'course-selection':
                content = `
                    <div class="card" id="course-selection">
                        <div class="card-header">
                            <div class="card-title">学生选课系统</div>
                        </div>
                        <div class="card-body">
                            <div class="course-selection-tabs">
                                <div class="tab-buttons">
                                    <button class="tab-btn active" data-tab="available-courses">
                                        <span class="tab-icon">📚</span>
                                        <span class="tab-text">可选课程</span>
                                    </button>
                                    <button class="tab-btn" data-tab="my-courses">
                                        <span class="tab-icon">📖</span>
                                        <span class="tab-text">我的课程</span>
                                    </button>
                                </div>

                                <div class="tab-content">
                                    <!-- 可选课程标签页 -->
                                    <div class="tab-pane active" id="available-courses">
                                        <div class="form-group" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                                            <div style="font-size: 14px; color: #7f8c8d;">找到 <span id="available-count" style="color: var(--primary-color); font-weight: 600;">0</span> 门可选课程</div>
                                            <button class="btn btn-accent" id="refresh-available-courses" style="display: flex; align-items: center; gap: 6px;">
                                                <span>🔄</span>
                                                <span>刷新可选课程</span>
                                            </button>
                                        </div>
                                        <div class="course-list" id="available-courses-list">
                                            <!-- 课程列表将在这里动态加载 -->
                                        </div>
                                    </div>

                                    <!-- 我的课程标签页 -->
                                    <div class="tab-pane" id="my-courses">
                                        <div class="form-group" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                                            <div style="font-size: 14px; color: #7f8c8d;">已选择 <span id="my-courses-count" style="color: var(--success-color); font-weight: 600;">0</span> 门课程</div>
                                            <button class="btn btn-secondary" id="refresh-my-courses" style="display: flex; align-items: center; gap: 6px;">
                                                <span>🔄</span>
                                                <span>刷新我的课程</span>
                                            </button>
                                        </div>
                                        <div class="course-list" id="my-courses-list">
                                            <!-- 已选课程列表将在这里动态加载 -->
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                setTimeout(initCourseSelectionPage, 100);
                break;
            case 'permission-assign':
                content = `
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">角色权限分配</div>
                            <button class="btn" onclick="loadPermissionAssignPage()">刷新</button>
                        </div>
                        <div class="card-body">
                            <div class="form-group">
                                <label for="permission-role-select">选择角色 <span class="required">*</span></label>
                                <select id="permission-role-select" onchange="onRoleSelected(this.value)">
                                    <option value="">请选择角色</option>
                                </select>
                            </div>
                            <div id="permission-content" style="display: none;">
                                <div style="margin: 20px 0; padding: 15px; background: #f5f5f5; border-radius: 4px;">
                                    <h3 id="selected-role-name" style="margin: 0 0 10px 0;"></h3>
                                    <p id="selected-role-desc" style="margin: 0; color: #666;"></p>
                                </div>
                                <div id="permission-tree" style="max-height: 600px; overflow-y: auto;">
                                    <!-- 权限树将在这里动态生成 -->
                                </div>
                                <div style="margin-top: 20px; text-align: right;">
                                    <button class="btn btn-secondary" onclick="selectAllPermissions()">全选</button>
                                    <button class="btn btn-secondary" onclick="deselectAllPermissions()">全不选</button>
                                    <button class="btn btn-accent" onclick="savePermissions()">保存权限</button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                setTimeout(initPermissionAssignPage, 100);
                break;
            default:
                content = `
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">${title}</div>
                        </div>
                        <div class="card-body">
                            <p>${title}功能正在开发中...</p>
                            <p>这里将实现${title}的相关功能。</p>
                        </div>
                    </div>
                `;
        }

        newPage.innerHTML = content;
        document.querySelector('.content-area').appendChild(newPage);
        return newPage;
    }
});

// ========== 学生管理功能 ==========

// 加载学生自己的信息（学生角色专用）
async function loadMyStudentInfo() {
    var container = document.getElementById('my-student-info');
    if (!container) return;
    
    var currentUser = getCurrentUser();
    if (!currentUser || !currentUser.studentNumber) {
        container.innerHTML = '<p style="text-align: center; color: #888;">无法获取学生信息</p>';
        return;
    }
    
    try {
        var student = await StudentAPI.getByNumber(currentUser.studentNumber);
        
        container.innerHTML = `
            <div style="max-width: 500px; margin: 0 auto;">
                <div style="display: grid; gap: 15px;">
                    <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                        <span style="color: #666;">学号</span>
                        <span style="font-weight: 500;">${student.studentNumber || '-'}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                        <span style="color: #666;">姓名</span>
                        <span style="font-weight: 500;">${student.studentName || '-'}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                        <span style="color: #666;">班级</span>
                        <span style="font-weight: 500;">${student.className || '-'}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                        <span style="color: #666;">用户名</span>
                        <span style="font-weight: 500;">${currentUser.username || '-'}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                        <span style="color: #666;">手机号</span>
                        <span style="font-weight: 500;">${currentUser.phoneNumber || '-'}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                        <span style="color: #666;">邮箱</span>
                        <span style="font-weight: 500;">${currentUser.email || '-'}</span>
                    </div>
                </div>
                <div style="margin-top: 20px; text-align: center;">
                    <button class="btn btn-accent" onclick="editMyStudentInfo(${student.studentId})">编辑我的信息</button>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('加载学生信息失败:', error);
        container.innerHTML = '<p style="text-align: center; color: red;">加载失败，请刷新重试</p>';
    }
}

// 编辑学生自己的信息
function editMyStudentInfo(studentId) {
    var currentUser = getCurrentUser();
    
    Modal.form({
        title: '✏️ 编辑我的信息',
        fields: [
            { name: 'studentName', label: '姓名', type: 'text', value: currentUser.realName || '', required: true },
            { name: 'className', label: '班级', type: 'text', value: currentUser.className || '' },
            { name: 'phoneNumber', label: '手机号', type: 'text', value: currentUser.phoneNumber || '' },
            { name: 'email', label: '邮箱', type: 'email', value: currentUser.email || '' }
        ],
        submitText: '保存',
        onSubmit: function(data) {
            StudentAPI.update(studentId, {
                studentName: data.studentName,
                className: data.className
            }).then(function() {
                showToast('信息更新成功！', 'success');
                // 更新localStorage中的用户信息
                currentUser.realName = data.studentName;
                currentUser.className = data.className;
                currentUser.phoneNumber = data.phoneNumber;
                currentUser.email = data.email;
                localStorage.setItem('currentUser', JSON.stringify(currentUser));
                updateUserDisplay();
                loadMyStudentInfo();
            }).catch(function(error) {
                console.error('更新失败:', error);
                showToast('更新失败: ' + error.message, 'error');
            });
        }
    });
}

let studentPageState = { page: 0, size: 10, totalPages: 1 };

// 加载所有学生（分页）
async function loadStudents() {
    const tbody = document.getElementById('student-table-body');
    if (!tbody) return;

    const pageSelect = document.getElementById('student-page-size');
    const currentPageSpan = document.getElementById('student-page-current');
    const totalPageSpan = document.getElementById('student-page-total');
    const btnPrev = document.getElementById('student-page-prev');
    const btnNext = document.getElementById('student-page-next');

    if (pageSelect) {
        studentPageState.size = parseInt(pageSelect.value, 10) || 10;
    }

    tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">加载中...</td></tr>';

    try {
        const resp = await StudentAPI.getPaged(studentPageState.page, studentPageState.size);
        const students = resp.content || [];
        studentPageState.totalPages = resp.totalPages || 1;
        studentPageState.page = resp.page || 0;

        if (!students || students.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">暂无学生数据</td></tr>';
            currentPageSpan && (currentPageSpan.textContent = studentPageState.page + 1);
            totalPageSpan && (totalPageSpan.textContent = studentPageState.totalPages);
            return;
        }

        tbody.innerHTML = students.map(student => `
            <tr>
                <td>${student.studentNumber || '-'}</td>
                <td>${student.studentName}</td>
                <td>${student.className || '-'}</td>
                <td>
                    <div class="btn-group">
                        <button class="btn" onclick="editStudent(${student.studentId})">编辑</button>
                        <button class="btn btn-danger" onclick="deleteStudent(${student.studentId})">删除</button>
                    </div>
                </td>
            </tr>
        `).join('');

        if (currentPageSpan) currentPageSpan.textContent = studentPageState.page + 1;
        if (totalPageSpan) totalPageSpan.textContent = studentPageState.totalPages;
        if (btnPrev) btnPrev.disabled = studentPageState.page <= 0;
        if (btnNext) btnNext.disabled = studentPageState.page >= studentPageState.totalPages - 1;
    } catch (error) {
        console.error('加载学生失败:', error);
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; color: red;">加载失败，请检查后端服务</td></tr>';
    }
}

// 添加学生
function addStudent() {
    Modal.form({
        title: '➕ 添加学生',
        fields: [
            { id: 'studentName', name: 'studentName', label: '姓名', type: 'text', required: true },
            { id: 'className', name: 'className', label: '班级', type: 'text', required: false },
            { id: 'phoneNumber', name: 'phoneNumber', label: '手机号', type: 'tel', required: false },
            { id: 'email', name: 'email', label: '邮箱', type: 'email', required: false }
        ],
        submitText: '添加',
        onSubmit: (formData) => {
            // 自动生成学号、用户名、密码
            const studentNumber = Generator.generateStudentNumber();
            const username = Generator.generateUsername(studentNumber);
            const password = Generator.generatePassword();
            
            // 创建用户账号数据
            const userData = Generator.createUserData({
                username: username,
                password: password,
                realName: formData.studentName,
                phoneNumber: formData.phoneNumber,
                email: formData.email,
                roleId: 3 // 学生角色
            });
            
            // 先创建用户账号
            UserAPI.create(userData)
                .then(newUser => {
                    // 创建学生记录
                    const student = {
                        studentNumber: studentNumber,
                        studentName: formData.studentName,
                        className: formData.className,
                        userId: newUser.userId
                    };
                    
                    return StudentAPI.create(student);
                })
                .then(() => {
                    // 显示账号信息
                    const accountInfo = Generator.showAccountInfo('student', studentNumber, username, password);
                    Modal.open({
                        title: '✅ 学生添加成功',
                        content: accountInfo,
                        showFooter: true,
                        submitText: '知道了',
                        cancelText: '复制账号信息',
                        onSubmit: () => {
                            loadStudents();
                        }
                    });
                })
                .catch(error => {
                    console.error('添加失败:', error);
                    showToast('添加失败：' + (error.message || '未知错误'), 'error');
                });
        }
    });
}

// 编辑学生
function editStudent(id) {
    StudentAPI.getById(id)
        .then(student => {
            Modal.form({
                title: '✏️ 编辑学生信息',
                fields: [
                    { id: 'studentNumber', name: 'studentNumber', label: '学号', type: 'text', value: student.studentNumber, required: true },
                    { id: 'studentName', name: 'studentName', label: '姓名', type: 'text', value: student.studentName, required: true },
                    { id: 'className', name: 'className', label: '班级', type: 'text', value: student.className, required: false }
                ],
                submitText: '保存',
                onSubmit: (formData) => {
                    const updatedStudent = {
                        studentNumber: formData.studentNumber,
                        studentName: formData.studentName,
                        className: formData.className,
                        userId: student.userId // 保持原有的用户ID，不允许修改
                    };
                    
                    StudentAPI.update(id, updatedStudent)
                        .then(() => {
                            showToast('学生信息更新成功！', 'success');
                            loadStudents();
                        })
                        .catch(error => {
                            console.error('更新失败:', error);
                            showToast('更新失败，请重试', 'error');
                        });
                }
            });
        })
        .catch(error => {
            console.error('获取学生信息失败:', error);
            showToast('获取学生信息失败', 'error');
        });
}

// 删除学生
function deleteStudent(id) {
    // 验证ID是否有效
    if (!id || id === 'undefined' || id === 'null' || (typeof id === 'string' && isNaN(id))) {
        showToast('无效的学生ID', 'error');
        return;
    }
    
    // 确保ID是数字类型
    const studentId = parseInt(id);
    if (isNaN(studentId) || studentId <= 0) {
        showToast('无效的学生ID', 'error');
        return;
    }
    
    Modal.confirm({
        title: '⚠️ 确认删除',
        message: '确定要删除这个学生吗？<br><br>如果该学生有选课记录，删除操作将失败。',
        submitText: '删除',
        danger: true,
        onConfirm: () => {
            StudentAPI.delete(studentId)
                .then((response) => {
                    const message = response?.message || '学生删除成功！';
                    showToast(message, 'success');
                    loadStudents();
                })
                .catch(error => {
                    console.error('删除失败:', error);
                    // 显示详细的错误信息
                    let errorMessage = '删除失败，请重试';
                    if (error.message) {
                        errorMessage = error.message;
                    } else if (error.response?.message) {
                        errorMessage = error.response.message;
                    }
                    
                    // 如果是409冲突错误（有选课记录），提供更详细的提示
                    if (error.status === 409 || errorMessage.includes('选课记录')) {
                        errorMessage = errorMessage + '<br><br>提示：请先删除或处理该学生的选课记录后再删除学生。';
                    }
                    
                    showToast(errorMessage, 'error');
                });
        }
    });
}

// ========== 教师管理功能 ==========

// 加载教师自己的信息
async function loadMyTeacherInfo() {
    var container = document.getElementById('my-teacher-info');
    if (!container) return;
    
    var currentUser = getCurrentUser();
    if (!currentUser) {
        container.innerHTML = '<p style="text-align: center; color: #888;">无法获取教师信息</p>';
        return;
    }
    
    container.innerHTML = `
        <div style="max-width: 500px; margin: 0 auto;">
            <div style="display: grid; gap: 15px;">
                <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                    <span style="color: #666;">工号</span>
                    <span style="font-weight: 500;">${currentUser.jobNumber || currentUser.username || '-'}</span>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                    <span style="color: #666;">姓名</span>
                    <span style="font-weight: 500;">${currentUser.realName || '-'}</span>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                    <span style="color: #666;">职称</span>
                    <span style="font-weight: 500;">${currentUser.jobTitle || '-'}</span>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                    <span style="color: #666;">所属院系</span>
                    <span style="font-weight: 500;">${currentUser.department || '-'}</span>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                    <span style="color: #666;">手机号</span>
                    <span style="font-weight: 500;">${currentUser.phoneNumber || '-'}</span>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                    <span style="color: #666;">邮箱</span>
                    <span style="font-weight: 500;">${currentUser.email || '-'}</span>
                </div>
            </div>
        </div>
    `;
}

// 加载所有教师（管理员用）- 从用户表中获取角色为教师的用户
async function loadTeachers() {
    var tbody = document.getElementById('teacher-table-body');
    if (!tbody) return;
    
    const pageSelect = document.getElementById('teacher-page-size');
    const currentPageSpan = document.getElementById('teacher-page-current');
    const totalPageSpan = document.getElementById('teacher-page-total');
    const btnPrev = document.getElementById('teacher-page-prev');
    const btnNext = document.getElementById('teacher-page-next');

    if (!window.teacherPageState) {
        window.teacherPageState = { page: 0, size: 10, totalPages: 1 };
    }
    if (pageSelect) {
        window.teacherPageState.size = parseInt(pageSelect.value, 10) || 10;
    }

    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">加载中...</td></tr>';
    
    try {
        // 角色ID 2 = 教师
        var resp = await UserAPI.getByRolePaged(2, window.teacherPageState.page, window.teacherPageState.size);
        var teachers = resp.content || [];
        window.teacherPageState.totalPages = resp.totalPages || 1;
        window.teacherPageState.page = resp.page || 0;

        if (teachers.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">暂无教师数据</td></tr>';
            currentPageSpan && (currentPageSpan.textContent = window.teacherPageState.page + 1);
            totalPageSpan && (totalPageSpan.textContent = window.teacherPageState.totalPages);
            return;
        }
        
        tbody.innerHTML = teachers.map(function(teacher) {
            return '<tr>' +
                '<td>' + (teacher.username || '-') + '</td>' +
                '<td>' + (teacher.realName || '-') + '</td>' +
                '<td>' + (teacher.phoneNumber || '-') + '</td>' +
                '<td>' + (teacher.email || '-') + '</td>' +
                '<td><div class="btn-group">' +
                    '<button class="btn" onclick="editTeacher(' + teacher.userId + ')">编辑</button>' +
                    '<button class="btn btn-danger" onclick="deleteTeacher(' + teacher.userId + ')">删除</button>' +
                '</div></td>' +
            '</tr>';
        }).join('');

        if (currentPageSpan) currentPageSpan.textContent = window.teacherPageState.page + 1;
        if (totalPageSpan) totalPageSpan.textContent = window.teacherPageState.totalPages;
        if (btnPrev) btnPrev.disabled = window.teacherPageState.page <= 0;
        if (btnNext) btnNext.disabled = window.teacherPageState.page >= window.teacherPageState.totalPages - 1;
    } catch (error) {
        console.error('加载教师失败:', error);
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: red;">加载失败</td></tr>';
    }
}

// ========== 课程管理功能 ==========

// 分页状态
let coursePageState = { page: 0, size: 10, totalPages: 1 };

// 加载教师自己的课程
async function loadMyCourses() {
    var container = document.getElementById('my-courses-container');
    if (!container) return;
    
    var currentUser = getCurrentUser();
    if (!currentUser) {
        container.innerHTML = '<p style="text-align: center; color: #888;">无法获取课程信息</p>';
        return;
    }
    
    try {
        // 获取所有课程班级，然后筛选当前教师的
        var classes = await API.get('/course-classes');
        var myCourses = classes.filter(function(c) {
            return c.teacherId === currentUser.userId;
        });
        
        if (myCourses.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #888;">暂无授课课程</p>';
            return;
        }
        
        var html = '<div class="table-container"><table><thead><tr>' +
            '<th>班级名称</th><th>上课时间</th><th>上课地点</th><th>学期</th>' +
        '</tr></thead><tbody>';
        
        myCourses.forEach(function(course) {
            html += '<tr>' +
                '<td>' + (course.className || '-') + '</td>' +
                '<td>' + (course.classTime || '-') + '</td>' +
                '<td>' + (course.classLocation || '-') + '</td>' +
                '<td>' + (course.semester || '-') + '</td>' +
            '</tr>';
        });
        
        html += '</tbody></table></div>';
        container.innerHTML = html;
    } catch (error) {
        console.error('加载课程失败:', error);
        container.innerHTML = '<p style="text-align: center; color: red;">加载失败</p>';
    }
}

// 加载所有课程（管理员和学生用，分页）
async function loadCourses() {
    var tbody = document.getElementById('course-table-body');
    if (!tbody) return;
    
    var currentUser = getCurrentUser();
    var isStudent = currentUser && currentUser.roleId === 3;
    var isAdmin = currentUser && currentUser.roleId === 1;
    var colSpan = isStudent ? 4 : 5;

    const pageSelect = document.getElementById('course-page-size');
    const currentPageSpan = document.getElementById('course-page-current');
    const totalPageSpan = document.getElementById('course-page-total');
    const btnPrev = document.getElementById('course-page-prev');
    const btnNext = document.getElementById('course-page-next');

    if (pageSelect) {
        coursePageState.size = parseInt(pageSelect.value, 10) || 10;
    }

    tbody.innerHTML = `<tr><td colspan="${colSpan}" style="text-align: center;">加载中...</td></tr>`;
    
    try {
        var resp = await CourseAPI.getPaged(coursePageState.page, coursePageState.size);
        var courses = resp.content || [];
        coursePageState.totalPages = resp.totalPages || 1;
        coursePageState.page = resp.page || 0;
        
        if (courses.length === 0) {
            tbody.innerHTML = `<tr><td colspan="${colSpan}" style="text-align: center;">暂无课程数据</td></tr>`;
            currentPageSpan && (currentPageSpan.textContent = coursePageState.page + 1);
            totalPageSpan && (totalPageSpan.textContent = coursePageState.totalPages);
            return;
        }
        
        tbody.innerHTML = courses.map(function(course) {
            var row = '<tr>' +
                '<td>' + (course.courseCode || '-') + '</td>' +
                '<td>' + (course.courseName || '-') + '</td>' +
                '<td>' + (course.credits || '-') + '</td>' +
                '<td>' + (course.semester || '-') + '</td>';
            
            if (!isStudent && isAdmin) {
                row += '<td><div class="btn-group">' +
                    '<button class="btn" onclick="editCourse(' + course.courseId + ')">编辑</button>' +
                    '<button class="btn btn-danger" onclick="deleteCourse(' + course.courseId + ')">删除</button>' +
                '</div></td>';
            }
            
            row += '</tr>';
            return row;
        }).join('');

        if (currentPageSpan) currentPageSpan.textContent = coursePageState.page + 1;
        if (totalPageSpan) totalPageSpan.textContent = coursePageState.totalPages;
        if (btnPrev) btnPrev.disabled = coursePageState.page <= 0;
        if (btnNext) btnNext.disabled = coursePageState.page >= coursePageState.totalPages - 1;
    } catch (error) {
        console.error('加载课程失败:', error);
        tbody.innerHTML = `<tr><td colspan="${colSpan}" style="text-align: center; color: red;">加载失败</td></tr>`;
    }
}

// ========== 用户管理功能 ==========

// 加载所有用户到表格
async function loadUsersTable() {
    const tbody = document.getElementById('user-table-body');
    if (!tbody) return;
    
    const pageSelect = document.getElementById('user-page-size');
    const currentPageSpan = document.getElementById('user-page-current');
    const totalPageSpan = document.getElementById('user-page-total');
    const btnPrev = document.getElementById('user-page-prev');
    const btnNext = document.getElementById('user-page-next');

    if (!window.userPageState) {
        window.userPageState = { page: 0, size: 10, totalPages: 1 };
    }
    if (pageSelect) {
        window.userPageState.size = parseInt(pageSelect.value, 10) || 10;
    }

    tbody.innerHTML = '<tr><td colspan="8" style="text-align: center;">加载中...</td></tr>';
    
    try {
        const resp = await UserAPI.getPaged(window.userPageState.page, window.userPageState.size);
        const users = resp.content || [];
        window.userPageState.totalPages = resp.totalPages || 1;
        window.userPageState.page = resp.page || 0;

        if (users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align: center;">暂无用户数据</td></tr>';
            currentPageSpan && (currentPageSpan.textContent = window.userPageState.page + 1);
            totalPageSpan && (totalPageSpan.textContent = window.userPageState.totalPages);
            return;
        }
        
        tbody.innerHTML = users.map(user => `
            <tr>
                <td>${user.userId}</td>
                <td>${user.username}</td>
                <td>${user.realName}</td>
                <td>${user.roleId === 1 ? '系统管理员' : user.roleId === 2 ? '教师' : user.roleId === 3 ? '学生' : '未知'}</td>
                <td>${user.phoneNumber || '-'}</td>
                <td>${user.email || '-'}</td>
                <td>${user.createdDate ? new Date(user.createdDate).toLocaleString('zh-CN') : '-'}</td>
                <td>
                    <div class="btn-group">
                        <button class="btn" onclick="editUser(${user.userId})">编辑</button>
                        <button class="btn btn-danger" onclick="deleteUser(${user.userId}, '${user.username}')">删除</button>
                    </div>
                </td>
            </tr>
        `).join('');

        if (currentPageSpan) currentPageSpan.textContent = window.userPageState.page + 1;
        if (totalPageSpan) totalPageSpan.textContent = window.userPageState.totalPages;
        if (btnPrev) btnPrev.disabled = window.userPageState.page <= 0;
        if (btnNext) btnNext.disabled = window.userPageState.page >= window.userPageState.totalPages - 1;
    } catch (error) {
        console.error('加载用户失败:', error);
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: red;">加载失败，请检查后端服务</td></tr>';
    }
}

// 添加用户
function addUser() {
    Modal.form({
        title: '添加用户',
        fields: [
            { id: 'username', name: 'username', label: '用户名', type: 'text', required: true },
            { id: 'password', name: 'password', label: '密码', type: 'password', required: true },
            { id: 'realName', name: 'realName', label: '真实姓名', type: 'text', required: true },
            { id: 'roleId', name: 'roleId', label: '角色', type: 'select', required: true,
              options: [
                  { value: '', label: '请选择角色' },
                  { value: '1', label: '系统管理员' },
                  { value: '2', label: '教师' },
                  { value: '3', label: '学生' }
              ]
            },
            { id: 'phoneNumber', name: 'phoneNumber', label: '手机号', type: 'tel', required: false },
            { id: 'email', name: 'email', label: '邮箱', type: 'email', required: false }
        ],
        submitText: '添加',
        onSubmit: (formData) => {
            const userData = {
                ...formData,
                roleId: parseInt(formData.roleId),
                isActive: 1
            };
            
            UserAPI.create(userData)
                .then(() => {
                    showToast('用户添加成功！', 'success');
                    loadUsersTable();
                })
                .catch(error => {
                    console.error('添加失败:', error);
                    showToast('添加失败：' + (error.message || '未知错误'), 'error');
                });
        }
    });
}

// 编辑用户
function editUser(id) {
    UserAPI.getById(id)
        .then(user => {
            Modal.form({
                title: '编辑用户信息',
                fields: [
                    { id: 'username', name: 'username', label: '用户名', type: 'text', value: user.username, required: true },
                    { id: 'realName', name: 'realName', label: '真实姓名', type: 'text', value: user.realName, required: true },
                    { id: 'roleId', name: 'roleId', label: '角色', type: 'select', value: String(user.roleId), required: true,
                      options: [
                          { value: '1', label: '系统管理员' },
                          { value: '2', label: '教师' },
                          { value: '3', label: '学生' }
                      ]
                    },
                    { id: 'phoneNumber', name: 'phoneNumber', label: '手机号', type: 'tel', value: user.phoneNumber || '', required: false },
                    { id: 'email', name: 'email', label: '邮箱', type: 'email', value: user.email || '', required: false }
                ],
                submitText: '保存',
                onSubmit: (formData) => {
                    const updatedUser = {
                        ...formData,
                        roleId: parseInt(formData.roleId)
                    };
                    
                    UserAPI.update(id, updatedUser)
                        .then(() => {
                            showToast('用户信息更新成功！', 'success');
                            loadUsersTable();
                        })
                        .catch(error => {
                            console.error('更新失败:', error);
                            showToast('更新失败，请重试', 'error');
                        });
                }
            });
        })
        .catch(error => {
            console.error('获取用户信息失败:', error);
            showToast('获取用户信息失败', 'error');
        });
}

// 删除用户
function deleteUser(id, username) {
    // 验证ID是否有效
    if (!id || id === 'undefined' || id === 'null' || (typeof id === 'string' && isNaN(id))) {
        showToast('无效的用户ID', 'error');
        return;
    }
    
    // 确保ID是数字类型
    const userId = parseInt(id);
    if (isNaN(userId) || userId <= 0) {
        showToast('无效的用户ID', 'error');
        return;
    }
    
    Modal.confirm({
        title: '确认删除用户',
        message: `确定要删除用户"${username}"吗？<br><br>此操作不可撤销。`,
        submitText: '删除',
        danger: true,
        onConfirm: () => {
            UserAPI.delete(userId)
                .then((response) => {
                    const message = response?.message || `用户"${username}"删除成功！`;
                    showToast(message, 'success');
                    loadUsersTable();
                })
                .catch(error => {
                    console.error('删除失败:', error);
                    // 显示详细的错误信息
                    const errorMessage = error.message || error.response?.message || '删除失败，请重试';
                    showToast(errorMessage, 'error');
                });
        }
    });
}

// ========== 角色管理功能 ==========

// 加载所有角色到表格
async function loadRolesTable() {
    const tbody = document.getElementById('role-table-body');
    if (!tbody) return;
    
    const pageSelect = document.getElementById('role-page-size');
    const currentPageSpan = document.getElementById('role-page-current');
    const totalPageSpan = document.getElementById('role-page-total');
    const btnPrev = document.getElementById('role-page-prev');
    const btnNext = document.getElementById('role-page-next');

    if (!window.rolePageState) {
        window.rolePageState = { page: 0, size: 10, totalPages: 1 };
    }
    if (pageSelect) {
        window.rolePageState.size = parseInt(pageSelect.value, 10) || 10;
    }

    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">加载中...</td></tr>';
    
    try {
        const resp = await RoleAPI.getPaged(window.rolePageState.page, window.rolePageState.size);
        const roles = resp.content || [];
        window.rolePageState.totalPages = resp.totalPages || 1;
        window.rolePageState.page = resp.page || 0;
        
        if (roles.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">暂无角色数据</td></tr>';
            currentPageSpan && (currentPageSpan.textContent = window.rolePageState.page + 1);
            totalPageSpan && (totalPageSpan.textContent = window.rolePageState.totalPages);
            return;
        }
        
        tbody.innerHTML = roles.map(role => `
            <tr>
                <td>${role.roleId}</td>
                <td>${role.roleName}</td>
                <td>${role.description || '-'}</td>
                <td>${role.createdTime ? new Date(role.createdTime).toLocaleString('zh-CN') : '-'}</td>
                <td>
                    <div class="btn-group">
                        <button class="btn btn-success" onclick="assignPermissions(${role.roleId}, '${role.roleName}')">分配权限</button>
                        <button class="btn" onclick="editRole(${role.roleId})">编辑</button>
                        <button class="btn btn-danger" onclick="deleteRole(${role.roleId}, '${role.roleName}')">删除</button>
                    </div>
                </td>
            </tr>
        `).join('');

        if (currentPageSpan) currentPageSpan.textContent = window.rolePageState.page + 1;
        if (totalPageSpan) totalPageSpan.textContent = window.rolePageState.totalPages;
        if (btnPrev) btnPrev.disabled = window.rolePageState.page <= 0;
        if (btnNext) btnNext.disabled = window.rolePageState.page >= window.rolePageState.totalPages - 1;
    } catch (error) {
        console.error('加载角色失败:', error);
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: red;">加载失败，请检查后端服务</td></tr>';
    }
}

// 添加角色
function addRole() {
    Modal.form({
        title: '➕ 添加角色',
        fields: [
            { id: 'roleName', name: 'roleName', label: '角色名称', type: 'text', required: true },
            { id: 'roleDesc', name: 'description', label: '角色描述', type: 'textarea', required: false }
        ],
        submitText: '添加',
        onSubmit: (formData) => {
            RoleAPI.create(formData)
                .then(() => {
                    showToast('角色添加成功！', 'success');
                    loadRolesTable();
                })
                .catch(error => {
                    console.error('添加失败:', error);
                    showToast('添加失败：' + (error.message || '未知错误'), 'error');
                });
        }
    });
}

// 编辑角色
function editRole(id) {
    RoleAPI.getById(id)
        .then(role => {
            Modal.form({
                title: '✏️ 编辑角色信息',
                fields: [
                    { id: 'roleName', name: 'roleName', label: '角色名称', type: 'text', value: role.roleName, required: true },
                    { id: 'roleDesc', name: 'description', label: '角色描述', type: 'textarea', value: role.description || '', required: false }
                ],
                submitText: '保存',
                onSubmit: (formData) => {
                    RoleAPI.update(id, formData)
                        .then(() => {
                            showToast('角色信息更新成功！', 'success');
                            loadRolesTable();
                        })
                        .catch(error => {
                            console.error('更新失败:', error);
                            showToast('更新失败，请重试', 'error');
                        });
                }
            });
        })
        .catch(error => {
            console.error('获取角色信息失败:', error);
            showToast('获取角色信息失败', 'error');
        });
}

// 删除角色
function deleteRole(id, roleName) {
    Modal.confirm({
        title: '⚠️ 确认删除角色',
        message: `确定要删除角色"${roleName}"吗？<br><br>此操作不可撤销，且会影响该角色下的所有用户。`,
        submitText: '删除',
        danger: true,
        onConfirm: () => {
            RoleAPI.delete(id)
                .then(() => {
                    showToast(`角色"${roleName}"删除成功！`, 'success');
                    loadRolesTable();
                })
                .catch(error => {
                    console.error('删除失败:', error);
                    showToast('删除失败，请重试', 'error');
                });
        }
    });
}

// 分配权限
function assignPermissions(roleId, roleName) {
    pendingPermissionRoleId = roleId;
    
    // 确保权限管理子菜单展开
    const permissionMenu = document.querySelector('.menu-item[data-target="permission"]');
    if (permissionMenu) {
        const submenu = permissionMenu.nextElementSibling;
        if (submenu && !submenu.classList.contains('show')) {
            permissionMenu.click();
        }
    }
    
    // 切换到权限分配页面
    const permissionPageEntry = document.querySelector('.submenu-item[data-target="permission-assign"]');
    if (!permissionPageEntry) {
        showToast('未找到权限分配页面入口', 'error');
        return;
    }
    permissionPageEntry.click();
    
    // 重新加载页面数据并尝试选中目标角色
    loadPermissionAssignPage();
    setTimeout(applyPendingPermissionRoleSelection, 200);
    
    showToast(`已为角色"${roleName}"打开权限分配页面`, 'info');
}

// 加载所有角色（保留旧的方法名，用于其他地方调用）
async function loadRoles() {
    try {
        const roles = await RoleAPI.getAll();
        console.log('角色列表:', roles);
        return roles;
    } catch (error) {
        console.error('加载角色失败:', error);
        return [];
    }
}

// 加载学生自己的考勤记录
async function loadMyAttendanceRecords() {
    var tbody = document.getElementById('my-attendance-body');
    if (!tbody) return;
    
    tbody.innerHTML = '<tr><td colspan="3" style="text-align: center;">加载中...</td></tr>';
    
    try {
        // 使用补齐缺勤的接口
        var records = await AttendanceRecordAPI.getMyRecordsFull();
        
        if (records.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" style="text-align: center;">暂无考勤记录</td></tr>';
            return;
        }
        
        tbody.innerHTML = records.map(function(record) {
            var time = record.checkInTime ? new Date(record.checkInTime).toLocaleString() : '-';
            var result = record.attendanceResult || '未签到';
            // 将非“正常”均视为失败展示红色
            var resultColor = result === '正常' ? 'green' : 'red';
            
            return '<tr>' +
                '<td>' + time + '</td>' +
                '<td style="color:' + resultColor + '">' + result + '</td>' +
                '<td>' + (record.remark || '-') + '</td>' +
            '</tr>';
        }).join('');
    } catch (error) {
        console.error('加载考勤记录失败:', error);
        tbody.innerHTML = '<tr><td colspan="3" style="text-align: center; color: red;">加载失败: ' + (error.message || '未知错误') + '</td></tr>';
    }
}

// ========== 通用工具函数 ==========

// 显示消息提示
function showMessage(message, type = 'info') {
    // 可以后续实现更美观的消息提示组件
    alert(message);
}

// 确认对话框
function showConfirm(message) {
    return confirm(message);
}

// ========== 课程管理功能 ==========

// 添加课程
function addCourse() {
    const tbody = document.getElementById('course-table-body');
    if (!tbody) {
        showToast('请先打开课程管理页面！', 'warning');
        return;
    }
    
    Modal.form({
        title: '➕ 添加课程',
        fields: [
            { id: 'courseCode', name: 'courseCode', label: '课程编号', type: 'text', required: true },
            { id: 'courseName', name: 'courseName', label: '课程名称', type: 'text', required: true },
            { id: 'teacher', name: 'teacher', label: '授课教师', type: 'text', required: true },
            { id: 'credits', name: 'credits', label: '学分', type: 'number', required: true }
        ],
        submitText: '添加',
        onSubmit: (formData) => {
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <td>${formData.courseCode}</td>
                <td>${formData.courseName}</td>
                <td>${formData.teacher}</td>
                <td>${formData.credits}</td>
                <td>
                    <div class="btn-group">
                        <button class="btn" onclick="editCourse('${formData.courseCode}', '${formData.courseName}', '${formData.teacher}', ${formData.credits})">编辑</button>
                        <button class="btn btn-danger" onclick="deleteCourse('${formData.courseCode}', '${formData.courseName}')">删除</button>
                    </div>
                </td>
            `;
            tbody.appendChild(newRow);
            showToast('课程添加成功！', 'success');
        }
    });
}

// 编辑课程
function editCourse(courseCode, courseName, teacher, credits) {
    Modal.form({
        title: '✏️ 编辑课程信息',
        fields: [
            { id: 'courseCode', name: 'courseCode', label: '课程编号', type: 'text', value: courseCode, required: true },
            { id: 'courseName', name: 'courseName', label: '课程名称', type: 'text', value: courseName, required: true },
            { id: 'teacher', name: 'teacher', label: '授课教师', type: 'text', value: teacher, required: true },
            { id: 'credits', name: 'credits', label: '学分', type: 'number', value: credits, required: true }
        ],
        submitText: '保存',
        onSubmit: (formData) => {
            // 查找并更新对应的行
            const tbody = document.getElementById('course-table-body');
            const rows = tbody.getElementsByTagName('tr');
            
            for (let row of rows) {
                const cells = row.getElementsByTagName('td');
                if (cells[0].textContent === courseCode) {
                    cells[0].textContent = formData.courseCode;
                    cells[1].textContent = formData.courseName;
                    cells[2].textContent = formData.teacher;
                    cells[3].textContent = formData.credits;
                    // 更新按钮的参数
                    const editBtn = cells[4].querySelector('.btn');
                    editBtn.onclick = function() { editCourse(formData.courseCode, formData.courseName, formData.teacher, formData.credits); };
                    break;
                }
            }
            
            showToast('课程信息更新成功！', 'success');
        }
    });
}

// 删除课程
function deleteCourse(courseCode, courseName) {
    Modal.confirm({
        title: '⚠️ 确认删除课程',
        message: `确定要删除课程吗？<br><br><strong>课程编号：</strong>${courseCode}<br><strong>课程名称：</strong>${courseName}<br><br>此操作不可撤销。`,
        submitText: '删除',
        danger: true,
        onConfirm: () => {
            const tbody = document.getElementById('course-table-body');
            const rows = tbody.getElementsByTagName('tr');
            
            for (let i = 0; i < rows.length; i++) {
                const cells = rows[i].getElementsByTagName('td');
                if (cells[0].textContent === courseCode) {
                    tbody.removeChild(rows[i]);
                    showToast(`课程 ${courseName} 删除成功！`, 'success');
                    return;
                }
            }
        }
    });
}

// ========== 教师管理功能 ==========

// 添加教师
function addTeacher() {
    const tbody = document.getElementById('teacher-table-body');
    if (!tbody) {
        showToast('请先打开教师管理页面！', 'warning');
        return;
    }
    
    Modal.form({
        title: '➕ 添加教师',
        fields: [
            { id: 'teacherName', name: 'teacherName', label: '教师姓名', type: 'text', required: true },
            { id: 'title', name: 'title', label: '职称', type: 'select', required: true, 
              options: [
                  { value: '', label: '请选择职称' },
                  { value: '讲师', label: '讲师' },
                  { value: '副教授', label: '副教授' },
                  { value: '教授', label: '教授' }
              ]
            },
            { id: 'department', name: 'department', label: '所属院系', type: 'text', required: true },
            { id: 'phoneNumber', name: 'phoneNumber', label: '手机号', type: 'tel', required: false },
            { id: 'email', name: 'email', label: '邮箱', type: 'email', required: false }
        ],
        submitText: '添加',
        onSubmit: (formData) => {
            // 自动生成工号、用户名、密码
            const teacherNumber = Generator.generateTeacherNumber();
            const username = Generator.generateUsername(teacherNumber);
            const password = Generator.generatePassword();
            
            // 创建用户账号数据
            const userData = Generator.createUserData({
                username: username,
                password: password,
                realName: formData.teacherName,
                phoneNumber: formData.phoneNumber,
                email: formData.email,
                roleId: 2 // 教师角色
            });
            
            // 先创建用户账号
            UserAPI.create(userData)
                .then(newUser => {
                    // 创建教师记录（这里使用静态表格，实际应该调用 TeacherAPI）
                    const newRow = document.createElement('tr');
                    newRow.innerHTML = `
                        <td>${teacherNumber}</td>
                        <td>${formData.teacherName}</td>
                        <td>${formData.title}</td>
                        <td>${formData.department}</td>
                        <td>
                            <div class="btn-group">
                                <button class="btn" onclick="editTeacher('${teacherNumber}', '${formData.teacherName}', '${formData.title}', '${formData.department}')">编辑</button>
                                <button class="btn btn-danger" onclick="deleteTeacher('${teacherNumber}', '${formData.teacherName}')">删除</button>
                            </div>
                        </td>
                    `;
                    tbody.appendChild(newRow);
                    
                    // 显示账号信息
                    const accountInfo = Generator.showAccountInfo('teacher', teacherNumber, username, password);
                    Modal.open({
                        title: '✅ 教师添加成功',
                        content: accountInfo,
                        showFooter: true,
                        submitText: '知道了',
                        cancelText: '复制账号信息',
                        onSubmit: () => {
                            // 刷新页面或重新加载数据
                        }
                    });
                })
                .catch(error => {
                    console.error('添加失败:', error);
                    showToast('添加失败：' + (error.message || '未知错误'), 'error');
                });
        }
    });
}

// 编辑教师
function editTeacher(teacherId, teacherName, title, department) {
    Modal.form({
        title: '✏️ 编辑教师信息',
        fields: [
            { id: 'teacherId', name: 'teacherId', label: '教师工号', type: 'text', value: teacherId, required: true },
            { id: 'teacherName', name: 'teacherName', label: '教师姓名', type: 'text', value: teacherName, required: true },
            { id: 'title', name: 'title', label: '职称', type: 'select', value: title, required: true,
              options: [
                  { value: '讲师', label: '讲师' },
                  { value: '副教授', label: '副教授' },
                  { value: '教授', label: '教授' }
              ]
            },
            { id: 'department', name: 'department', label: '所属院系', type: 'text', value: department, required: true }
        ],
        submitText: '保存',
        onSubmit: (formData) => {
            const tbody = document.getElementById('teacher-table-body');
            const rows = tbody.getElementsByTagName('tr');
            
            for (let row of rows) {
                const cells = row.getElementsByTagName('td');
                if (cells[0].textContent === teacherId) {
                    cells[0].textContent = formData.teacherId;
                    cells[1].textContent = formData.teacherName;
                    cells[2].textContent = formData.title;
                    cells[3].textContent = formData.department;
                    break;
                }
            }
            
            showToast('教师信息更新成功！', 'success');
        }
    });
}

// 删除教师
function deleteTeacher(teacherId, teacherName) {
    Modal.confirm({
        title: '⚠️ 确认删除教师',
        message: `确定要删除教师吗？<br><br><strong>工号：</strong>${teacherId}<br><strong>姓名：</strong>${teacherName}<br><br>此操作不可撤销。`,
        submitText: '删除',
        danger: true,
        onConfirm: () => {
            const tbody = document.getElementById('teacher-table-body');
            const rows = tbody.getElementsByTagName('tr');
            
            for (let i = 0; i < rows.length; i++) {
                const cells = rows[i].getElementsByTagName('td');
                if (cells[0].textContent === teacherId) {
                    tbody.removeChild(rows[i]);
                    showToast(`教师 ${teacherName} 删除成功！`, 'success');
                    return;
                }
            }
        }
    });
}

// ========== 考勤任务功能 ==========

// 初始化发布考勤任务页面
function initPublishTaskPage() {
    const form = document.getElementById('publish-task-form');
    const select = document.getElementById('task-class-select');
    const btnSearchLocation = document.getElementById('btn-search-location');
    
    // 辅助函数：更新隐藏的经纬度输入框（用于表单提交到数据库）
    function updateCoordinates(lat, lng) {
        const latHidden = document.getElementById('latitude');
        const lngHidden = document.getElementById('longitude');
        
        if (latHidden) latHidden.value = lat.toFixed(7);
        if (lngHidden) lngHidden.value = lng.toFixed(7);
    }
    
    // 设置默认时间
    const now = new Date();
    // 设置默认时间为当前时间 (HH:mm)
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const currentTimeStr = `${hours}:${minutes}`;
    
    const startTimeInput = document.getElementById('task-start-time');
    // const endTimeInput = document.getElementById('task-end-time'); // 已移除
    
    if (startTimeInput) startTimeInput.value = currentTimeStr;
    // if (endTimeInput) endTimeInput.value = end; // 已移除

    // 加载班级
    CourseClassAPI.getAll().then(classes => {
        if (!classes || classes.length === 0) {
            select.innerHTML = '<option value="">暂无班级数据</option>';
            return;
        }
        select.innerHTML = '<option value="">请选择班级</option>' + 
            classes.map(c => `<option value="${c.classId}">${c.className} (ID:${c.classId})</option>`).join('');
    }).catch(err => {
        console.error(err);
        select.innerHTML = '<option value="">加载失败</option>';
        showToast('加载班级失败，请检查网络或后端服务', 'error');
    });

    // 初始化地图函数
    function initMap(lat, lng) {
        const mapContainer = document.getElementById('map-container');
        if (!mapContainer) return;
        
        // 如果已经初始化过地图，先销毁
        if (window.currentMap) {
            window.currentMap.destroy();
            window.currentMap = null;
            window.currentMarker = null;
        }
        
        // 验证并转换坐标值
        const numLat = parseFloat(lat);
        const numLng = parseFloat(lng);
        const isValidLat = !isNaN(numLat) && numLat >= -90 && numLat <= 90;
        const isValidLng = !isNaN(numLng) && numLng >= -180 && numLng <= 180;
        
        // 默认位置：如果没有提供有效坐标，则默认为北京
        const defaultLat = isValidLat ? numLat : 39.9042;
        const defaultLng = isValidLng ? numLng : 116.4074;
        const zoomLevel = (isValidLat && isValidLng) ? 16 : 12;

        // 使用高德地图初始化
        // 如果 AMap 已经加载，直接创建地图；否则先加载 AMap
        if (window.AMapInstance) {
            return Promise.resolve(createAMapInstance(window.AMapInstance, defaultLng, defaultLat, zoomLevel, 
                isValidLat ? numLat : null, isValidLng ? numLng : null));
        } else {
            // 先加载 AMap 对象（不创建地图实例）
            return initAMap(null, null).then((AMap) => {
                window.AMapInstance = AMap;
                return createAMapInstance(AMap, defaultLng, defaultLat, zoomLevel, 
                    isValidLat ? numLat : null, isValidLng ? numLng : null);
            }).catch((e) => {
                console.error('高德地图加载失败:', e);
                mapContainer.innerHTML = '<div style="padding: 20px; text-align: center; color: #666;">地图加载失败，请检查网络连接</div>';
                throw e;
            });
        }
    }
    
    // 创建高德地图实例的辅助函数
    function createAMapInstance(AMap, defaultLng, defaultLat, zoomLevel, lat, lng) {
        // 确保坐标是有效的数字
        const validLng = typeof defaultLng === 'number' && !isNaN(defaultLng) ? defaultLng : 116.4074;
        const validLat = typeof defaultLat === 'number' && !isNaN(defaultLat) ? defaultLat : 39.9042;
        const validZoom = typeof zoomLevel === 'number' && !isNaN(zoomLevel) ? zoomLevel : 12;
        
        const map = new AMap.Map('map-container', {
            zoom: validZoom,
            center: [validLng, validLat],
            viewMode: '3D'
        });
        
        window.currentMap = map;
        
        // 添加比例尺
        map.addControl(new AMap.Scale());
        
        // 先删除旧的标记
        if (window.currentMarker) {
            window.currentMarker.setMap(null);
            window.currentMarker = null;
        }
        
        // 验证lat和lng是否有效
        const numLat = parseFloat(lat);
        const numLng = parseFloat(lng);
        const hasValidCoords = !isNaN(numLat) && !isNaN(numLng) && 
                               numLat >= -90 && numLat <= 90 && 
                               numLng >= -180 && numLng <= 180;
        
        // 如果有有效坐标，创建标记
        if (hasValidCoords) {
            window.currentMarker = new AMap.Marker({
                position: [numLng, numLat],
                draggable: true
            });
            window.currentMarker.setMap(map);
        }

        // 地图点击事件
        map.on('click', async function(e) {
                const clickedLng = e.lnglat.lng;
                const clickedLat = e.lnglat.lat;
                
                // 更新可见和隐藏的经纬度输入框
                updateCoordinates(clickedLat, clickedLng);
                
                // 更新或创建标记（确保只有一个标记）
                if (window.currentMarker) {
                    // 如果已存在标记，只更新位置
                    window.currentMarker.setPosition([clickedLng, clickedLat]);
                } else {
                    // 如果不存在标记，创建新标记
                    window.currentMarker = new AMap.Marker({
                        position: [clickedLng, clickedLat],
                        draggable: true
                    });
                    window.currentMarker.setMap(map);
                }
                
                // 高德地图逆地理编码获取地址
                try {
                    showToast('正在获取地址信息...', 'info', 1000);
                    const geocoder = new AMap.Geocoder();
                    geocoder.getAddress([clickedLng, clickedLat], (status, result) => {
                        if (status === 'complete' && result.info === 'OK') {
                            const address = result.regeocode.formattedAddress;
                            const locationRangeInput = document.getElementById('location-range');
                            if (locationRangeInput) {
                                // 简化地址显示（取前两个部分）
                                const addressParts = address.split(/省|市|区|县|街道|路|号/);
                                const simplifiedAddress = addressParts.slice(0, 3).join('') || address;
                                locationRangeInput.value = simplifiedAddress;
                                
                                // 设置标记信息窗口
                                if (window.currentMarker) {
                                    window.currentMarker.setTitle(simplifiedAddress);
                                    window.currentMarker.setLabel({
                                        content: simplifiedAddress,
                                        direction: 'right'
                                    });
                                }
                            }
                        }
                    });
                } catch (err) {
                    console.warn('逆地理编码失败:', err);
                }
        });
        
        // 标记拖拽事件
        if (window.currentMarker) {
            window.currentMarker.on('dragend', async function(e) {
                    const position = window.currentMarker.getPosition();
                    const lat = position.getLat();
                    const lng = position.getLng();
                    
                    // 更新可见和隐藏的经纬度输入框
                    updateCoordinates(lat, lng);
                    
                    // 拖拽后更新地址
                    try {
                        const geocoder = new AMap.Geocoder();
                        geocoder.getAddress([lng, lat], (status, result) => {
                            if (status === 'complete' && result.info === 'OK') {
                                const address = result.regeocode.formattedAddress;
                                const locationRangeInput = document.getElementById('location-range');
                                if (locationRangeInput) {
                                    const addressParts = address.split(/省|市|区|县|街道|路|号/);
                                    const simplifiedAddress = addressParts.slice(0, 3).join('') || address;
                                    locationRangeInput.value = simplifiedAddress;
                                    if (window.currentMarker) {
                                        window.currentMarker.setTitle(simplifiedAddress);
                                        window.currentMarker.setLabel({
                                            content: simplifiedAddress,
                                            direction: 'right'
                                        });
                                    }
                                }
                            }
                        });
            } catch (err) {
                console.warn('逆地理编码失败:', err);
            }
        });
        }
        
        return map;
    }
    
    // 处理地图加载错误的辅助函数
    function handleMapError(mapContainer, e) {
        console.error('高德地图加载失败:', e);
        mapContainer.innerHTML = '<div style="padding: 20px; text-align: center; color: #666;">地图加载失败，请检查网络连接</div>';
    }

    // 页面加载完成后初始化地图（尝试获取位置，如果失败则显示默认地图）
    setTimeout(() => {
        const mapContainer = document.getElementById('map-container');
        if (!mapContainer) return;
        
        // 先初始化地图，然后尝试获取定位
        initMap().then(map => {
            if (!map || !window.AMapInstance) {
                return;
            }
            
            // 使用高德地图定位插件获取当前位置
            window.AMapInstance.plugin('AMap.Geolocation', function() {
                const geolocation = new window.AMapInstance.Geolocation({
                    enableHighAccuracy: true,      // 进行浏览器原生定位的时候是否尝试获取较高精度
                    timeout: 20000,                 // 定位的超时时间，毫秒
                    maximumAge: 0,                  // 浏览器原生定位的缓存时间，毫秒
                    convert: true,                  // 是否将定位结果转换为高德坐标
                    showButton: false,              // 不显示定位按钮
                    showMarker: false,              // 不显示定位点
                    showCircle: false,              // 不显示定位精度圆
                    panToLocation: false,           // 定位成功后不自动移动到响应位置
                    zoomToAccuracy: false,          // 定位成功后不自动调整级别
                    GeoLocationFirst: true,          // 优先使用H5定位
                    getCityWhenFail: true,          // 定位失败之后返回基本城市定位信息
                    needAddress: true,              // 需要将定位结果进行逆地理编码操作
                    extensions: 'base'              // 返回基本的逆地理编码信息
                });
                
                geolocation.getCurrentPosition(function(status, result) {
                    if (status === 'complete' && result.info === 'SUCCESS') {
                        // 定位成功
                        const lat = result.position.getLat();
                        const lng = result.position.getLng();
                        
                        // 更新可见和隐藏的经纬度输入框
                        updateCoordinates(lat, lng);
                        
                        // 获取定位类型描述
                        const locationTypes = {
                            1: 'IP定位',
                            2: 'H5定位',
                            3: 'SDK定位',
                            4: 'IP城市定位'
                        };
                        const locationType = locationTypes[result.location_type] || '未知类型';
                        
                        // 更新地图中心点和标记
                        if (window.currentMap) {
                            // 根据定位类型和精度调整缩放级别
                            let zoomLevel = 16;
                            if (result.location_type === 1 || result.location_type === 4) {
                                // IP定位精度较低
                                zoomLevel = 12;
                            } else if (result.accuracy) {
                                // 根据精度调整缩放级别
                                if (result.accuracy > 1000) zoomLevel = 12;
                                else if (result.accuracy > 500) zoomLevel = 14;
                                else if (result.accuracy > 100) zoomLevel = 15;
                            }
                            
                            window.currentMap.setCenter([lng, lat]);
                            window.currentMap.setZoom(zoomLevel);
                            
                            // 如果已有标记，更新位置；否则创建新标记
                            if (window.currentMarker) {
                                window.currentMarker.setPosition([lng, lat]);
                            } else {
                                window.currentMarker = new window.AMapInstance.Marker({
                                    position: [lng, lat],
                                    draggable: true,
                                    map: window.currentMap
                                });
                            }
                            
                            // 处理地址信息（needAddress为true时，result中已包含地址信息）
                            let address = '';
                            if (result.formattedAddress) {
                                address = result.formattedAddress;
                            } else if (result.addressComponent) {
                                // 从结构化地址组件构建地址
                                const addr = result.addressComponent;
                                address = (addr.province || '') + (addr.city || '') + (addr.district || '') + (addr.street || '');
                            }
                            
                            if (address) {
                                const locationRangeInput = document.getElementById('location-range');
                                if (locationRangeInput) {
                                    const addressParts = address.split(/省|市|区|县|街道|路|号/);
                                    const simplifiedAddress = addressParts.slice(0, 3).join('') || address;
                                    locationRangeInput.value = simplifiedAddress;
                                    
                                    if (window.currentMarker) {
                                        const markerTitle = simplifiedAddress + (result.location_type === 1 || result.location_type === 4 ? ' (IP定位)' : '');
                                        window.currentMarker.setTitle(markerTitle);
                                        window.currentMarker.setLabel({
                                            content: simplifiedAddress,
                                            direction: 'right'
                                        });
                                    }
                                }
                            }
                            
                            console.log('定位成功:', {
                                type: locationType,
                                accuracy: result.accuracy ? result.accuracy + '米' : '未知',
                                address: address
                            });
                        }
                    } else {
                        // 定位失败
                        const errorMsg = '失败原因排查信息: ' + (result.message || '未知错误');
                        const originMsg = result.originMessage ? '<br>浏览器返回信息: ' + result.originMessage : '';
                        const infoMsg = result.info ? '<br>错误代码: ' + result.info : '';
                        console.error('定位失败详情:', errorMsg + originMsg + infoMsg);
                        
                        // 如果设置了getCityWhenFail，可能返回城市信息
                        if (result.formattedAddress) {
                            console.log('获取到城市信息:', result.formattedAddress);
                        }
                    }
                });
            });
        }).catch(e => {
            handleMapError(mapContainer, e);
        });
    }, 500);

    // 全局函数：处理时长选择
    window.updateDurationInput = function(value) {
        const input = document.getElementById('task-duration-input');
        if (value === 'custom') {
            input.style.display = 'block';
            input.value = '';
            input.focus();
        } else {
            input.style.display = 'none';
            input.value = value;
        }
    };

    // 搜索地点功能（使用高德地图搜索）
    if (btnSearchLocation) {
        btnSearchLocation.addEventListener('click', async () => {
            const locationInput = document.getElementById('location-range');
            const query = locationInput.value.trim();
            
            if (!query) {
                showToast('请输入要搜索的地点名称', 'warning');
                return;
            }
            
            btnSearchLocation.textContent = '搜索中...';
            btnSearchLocation.disabled = true;
            
            try {
                // 确保地图已初始化
                if (!window.currentMap || !window.AMapInstance) {
                    await initMap();
                }
                
                const AMap = window.AMapInstance;
                const map = window.currentMap;
                
                // 使用高德地图搜索
                const placeSearch = new AMap.PlaceSearch({
                    city: '全国', // 搜索范围
                    pageSize: 5,
                    pageIndex: 1
                });
                
                placeSearch.search(query, (status, result) => {
                    if (status === 'complete' && result.info === 'OK') {
                        if (result.poiList && result.poiList.pois && result.poiList.pois.length > 0) {
                            const poi = result.poiList.pois[0];
                            const lat = poi.location.lat;
                            const lng = poi.location.lng;
                            
                            // 更新可见和隐藏的经纬度输入框
                            updateCoordinates(lat, lng);
                            
                            // 更新地图中心点和标记
                            map.setCenter([lng, lat]);
                            map.setZoom(16);
                            
                            // 更新或创建标记（确保只有一个标记）
                            if (window.currentMarker) {
                                // 如果已存在标记，只更新位置和标签
                                window.currentMarker.setPosition([lng, lat]);
                                window.currentMarker.setTitle(poi.name);
                                window.currentMarker.setLabel({
                                    content: poi.name,
                                    direction: 'right'
                                });
                            } else {
                                // 如果不存在标记，创建新标记
                                window.currentMarker = new AMap.Marker({
                                    position: [lng, lat],
                                    draggable: true,
                                    title: poi.name
                                });
                                window.currentMarker.setMap(map);
                                window.currentMarker.setLabel({
                                    content: poi.name,
                                    direction: 'right'
                                });
                                
                                // 绑定拖拽事件
                                window.currentMarker.on('dragend', function(e) {
                                    const position = window.currentMarker.getPosition();
                                    const dragLat = position.getLat();
                                    const dragLng = position.getLng();
                                    updateCoordinates(dragLat, dragLng);
                                    
                                    // 拖拽后更新地址
                                    try {
                                        const geocoder = new AMap.Geocoder();
                                        geocoder.getAddress([dragLng, dragLat], (status, result) => {
                                            if (status === 'complete' && result.info === 'OK') {
                                                const address = result.regeocode.formattedAddress;
                                                const locationRangeInput = document.getElementById('location-range');
                                                if (locationRangeInput) {
                                                    const addressParts = address.split(/省|市|区|县|街道|路|号/);
                                                    const simplifiedAddress = addressParts.slice(0, 3).join('') || address;
                                                    locationRangeInput.value = simplifiedAddress;
                                                    if (window.currentMarker) {
                                                        window.currentMarker.setTitle(simplifiedAddress);
                                                        window.currentMarker.setLabel({
                                                            content: simplifiedAddress,
                                                            direction: 'right'
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    } catch (err) {
                                        console.warn('逆地理编码失败:', err);
                                    }
                                });
                            }
                            
                            showToast('已定位到搜索地点', 'success');
                        } else {
                            showToast('未找到相关地点，请尝试更详细的描述', 'warning');
                        }
                    } else {
                        showToast('搜索服务暂不可用', 'error');
                    }
                    
                    btnSearchLocation.textContent = '搜索';
                    btnSearchLocation.disabled = false;
                });
            } catch (e) {
                console.error('搜索失败:', e);
                showToast('搜索发生错误，请检查网络', 'error');
                btnSearchLocation.textContent = '搜索';
                btnSearchLocation.disabled = false;
            }
        });
    }

        // 处理提交
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formData = new FormData(form);

        // 计算开始和结束时间
        const startTimeValue = formData.get('startTime'); // HH:mm
        const duration = parseInt(document.getElementById('task-duration-input').value) || 10;
        
        // 构造完整的开始时间 (今天日期 + 选择的时间)
        const now = new Date();
        const [hours, minutes] = startTimeValue.split(':').map(Number);
        const startDateTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), hours, minutes);
        
        // 如果选择的时间已经过去了（比如现在10点，选择了9点），是否默认为明天？
        // 暂时按当天处理，如果是补签或测试可能需要过去时间
        
        // 构造结束时间
        const endDateTime = new Date(startDateTime.getTime() + duration * 60000);
        
        // 处理时区偏移，转换为 ISO 字符串发送给后端
        // 注意：后端 AttendanceTask.java 使用 LocalDateTime，通常期望不带时区的 ISO 格式 (YYYY-MM-DDTHH:mm:ss)
        // 或者前端手动拼接格式
        
        const formatDate = (date) => {
            const pad = (n) => String(n).padStart(2, '0');
            return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
        };

        const task = {
            courseClassId: parseInt(formData.get('courseClassId')),
            taskName: formData.get('taskName'),
            startTime: formatDate(startDateTime),
            endTime: formatDate(endDateTime),
            locationRange: formData.get('locationRange'),
            latitude: parseFloat(formData.get('latitude')),
            longitude: parseFloat(formData.get('longitude')),
            radius: parseInt(formData.get('radius')),
            teacherId: 1 // 暂时硬编码教师ID
        };
        
        try {
            await AttendanceTaskAPI.create(task);
            showToast('考勤发布成功！', 'success');
            form.reset();
            // 重置时间为当前时间
            if (startTimeInput) {
                const now = new Date();
                const h = String(now.getHours()).padStart(2, '0');
                const m = String(now.getMinutes()).padStart(2, '0');
                startTimeInput.value = `${h}:${m}`;
            }
            // 重置时长选择
            document.getElementById('task-duration-select').value = '10';
            document.getElementById('task-duration-input').style.display = 'none';
            document.getElementById('task-duration-input').value = '10';
            
            // 隐藏地图
            const mapContainer = document.getElementById('map-container');
            if (mapContainer) {
                mapContainer.style.display = 'none';
            }
        } catch (err) {
            console.error(err);
            showToast('发布失败：' + (err.message || '未知错误'), 'error');
        }
    });
}

// ========== 权限分配功能 ==========

let currentRoleId = null;
let allFunctions = [];
let rolePermissions = new Set();
let pendingPermissionRoleId = null;

// 初始化权限分配页面
async function initPermissionAssignPage() {
    await loadRolesForPermission();
}

// 加载角色列表到下拉框
async function loadRolesForPermission() {
    const select = document.getElementById('permission-role-select');
    if (!select) return;
    
    try {
        const roles = await RoleAPI.getAll();
        select.innerHTML = '<option value="">请选择角色</option>' +
            roles.map(role => `<option value="${role.roleId}">${role.roleName}</option>`).join('');
        
        // 如果有待跳转的角色，尝试自动选中
        applyPendingPermissionRoleSelection();
    } catch (error) {
        console.error('加载角色失败:', error);
        showToast('加载角色失败', 'error');
    }
}

// 角色选择变化事件
async function onRoleSelected(roleId) {
    if (!roleId) {
        document.getElementById('permission-content').style.display = 'none';
        currentRoleId = null;
        return;
    }
    
    currentRoleId = parseInt(roleId);
    await loadPermissionData();
}

// 加载权限数据
async function loadPermissionData() {
    if (!currentRoleId) return;
    
    try {
        // 加载所有功能
        allFunctions = await FunctionAPI.getActive();
        
        // 加载角色已有的权限
        const permissions = await PermissionAPI.getRolePermissions(currentRoleId);
        rolePermissions = new Set(permissions.map(p => p.functionId));
        
        // 加载角色信息
        const role = await RoleAPI.getById(currentRoleId);
        document.getElementById('selected-role-name').textContent = `当前角色：${role.roleName}`;
        document.getElementById('selected-role-desc').textContent = role.description || '暂无描述';
        
        // 渲染权限树
        renderPermissionTree();
        
        // 显示权限内容
        document.getElementById('permission-content').style.display = 'block';
    } catch (error) {
        console.error('加载权限数据失败:', error);
        showToast('加载权限数据失败', 'error');
    }
}

// 渲染权限树（按照用户提供的权限设计组织）
function renderPermissionTree() {
    const container = document.getElementById('permission-tree');
    if (!container) return;
    
    // 按照模块分组权限
    const modules = groupFunctionsByModule(allFunctions);
    
    let html = '';
    
    // 根据当前选择的角色显示对应的权限分组
    if (currentRoleId === 3 || currentRoleId === 1) { // 学生或管理员
        html += renderPermissionSection('学生权限', [
            {
                title: '课程相关',
                permissions: [
                    { code: 'course.view', name: '查看课程', desc: '只能查看自己已选的课程列表' },
                    { code: 'course.view', name: '查看课程详情', desc: '只能查看自己课程的教学大纲、课件、作业等' },
                    { code: 'course.add', name: '选课操作', desc: '在选课期内选择未满员的课程' },
                    { code: 'course.delete', name: '退课操作', desc: '在规定时间内退选已选课程' }
                ]
            },
            {
                title: '个人信息',
                permissions: [
                    { code: 'user.edit', name: '修改个人信息', desc: '修改联系方式、密码等基础信息' },
                    { code: 'dashboard.view', name: '查看个人课表', desc: '查看自己的课程时间安排' }
                ]
            }
        ], 3);
    }
    
    if (currentRoleId === 2 || currentRoleId === 1) { // 教师或管理员
        html += renderPermissionSection('教师权限', [
            {
                title: '课程管理',
                permissions: [
                    { code: 'course.view', name: '查看所教课程', desc: '只能查看自己负责的课程' },
                    { code: 'course.edit', name: '课程内容管理', desc: '管理自己课程的教学资料、课件、视频等' },
                    { code: 'attendance_task.add', name: '发布通知', desc: '向自己课程的学生发布课程通知' },
                    { code: 'course.edit', name: '设置课程信息', desc: '修改自己课程的基本信息（除课程代码等核心信息）' }
                ]
            },
            {
                title: '学生管理',
                permissions: [
                    { code: 'student.view', name: '查看选课学生', desc: '查看选择自己课程的学生名单' },
                    { code: 'attendance.edit', name: '成绩管理', desc: '录入、修改、发布自己课程的学生成绩' },
                    { code: 'attendance.view_all', name: '考勤记录', desc: '记录和管理学生的出勤情况' }
                ]
            }
        ], 2);
    }
    
    if (currentRoleId === 1) { // 管理员
        html += renderPermissionSection('管理员权限', [
            {
                title: '用户管理',
                permissions: [
                    { code: 'user.view', name: '用户账户管理', desc: '创建、修改、禁用学生和教师账户' },
                    { code: 'user.assign_role', name: '角色分配', desc: '为用户分配学生、教师或管理员角色' },
                    { code: 'user.add', name: '批量操作', desc: '批量导入用户信息' }
                ]
            },
            {
                title: '课程体系管理',
                permissions: [
                    { code: 'course.add', name: '课程创建/删除', desc: '创建新课程或删除旧课程' },
                    { code: 'class.add', name: '课程分配', desc: '将课程分配给特定教师授课' },
                    { code: 'course.edit', name: '课程属性设置', desc: '设置课程代码、学分、容量等核心信息' }
                ]
            },
            {
                title: '系统管理',
                permissions: [
                    { code: 'permission.assign', name: '权限管理', desc: '定义和修改各角色的权限范围' },
                    { code: 'dashboard.view', name: '数据维护', desc: '备份和恢复系统数据' },
                    { code: 'dashboard.view', name: '系统监控', desc: '监控系统运行状态和用户活动' },
                    { code: 'dashboard.view', name: '全局设置', desc: '设置学期、选课时间等系统参数' }
                ]
            }
        ], 1);
    }
    
    // 其他权限（从数据库加载的实际权限，按模块显示）
    html += renderOtherPermissions(modules);
    
    container.innerHTML = html;
}

// 渲染权限分组
function renderPermissionSection(title, groups, roleId) {
    let html = `<div class="permission-section" style="margin-bottom: 30px;">
        <h3 style="margin-bottom: 15px; color: var(--primary-color);">${title}</h3>`;
    
    groups.forEach(group => {
        html += `<div class="permission-group" style="margin-bottom: 20px;">
            <h4 style="margin-bottom: 10px; font-size: 1.1em; color: #666;">${group.title}</h4>
            <div class="permission-list" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 12px;">`;
        
        group.permissions.forEach(perm => {
            // 查找匹配的功能（可能多个功能有相同的代码，取第一个）
            const matchingFunctions = allFunctions.filter(f => f.functionCode === perm.code);
            const functionId = matchingFunctions.length > 0 ? matchingFunctions[0].functionId : null;
            const isChecked = functionId && rolePermissions.has(functionId);
            
            // 如果找不到对应的功能，使用功能代码作为标识
            const displayFunctionId = functionId || perm.code;
            
            html += `
                <label class="permission-item" style="display: flex; align-items: flex-start; padding: 14px; border: 1px solid #ddd; border-radius: 6px; cursor: pointer; background: ${isChecked ? '#e8f5e9' : '#fff'}; transition: all 0.2s; min-width: 0;">
                    <input type="checkbox" 
                           class="permission-checkbox" 
                           data-function-id="${displayFunctionId}" 
                           data-function-code="${perm.code}"
                           ${isChecked ? 'checked' : ''}
                           ${!functionId ? 'disabled title="该功能在系统中不存在"' : ''}
                           style="margin-right: 12px; margin-top: 2px; flex-shrink: 0; width: 18px; height: 18px; cursor: pointer;">
                    <div style="flex: 1; min-width: 0; word-wrap: break-word; word-break: break-word; white-space: normal; line-height: 1.5;">
                        <div style="font-weight: 500; margin-bottom: 6px; color: #2c3e50; font-size: 14px;">${perm.name}${!functionId ? ' <span style="color: #999; font-size: 0.85em;">(未配置)</span>' : ''}</div>
                        <div style="font-size: 0.85em; color: #666; line-height: 1.4;">${perm.desc}</div>
                    </div>
                </label>
            `;
        });
        
        html += `</div></div>`;
    });
    
    html += `</div>`;
    return html;
}

// 渲染其他权限（从数据库加载）
function renderOtherPermissions(modules) {
    let html = '<div class="permission-section" style="margin-top: 30px;"><h3 style="margin-bottom: 15px; color: var(--primary-color);">其他功能权限</h3>';
    
    Object.keys(modules).sort().forEach(moduleName => {
        html += `<div class="permission-group" style="margin-bottom: 20px;">
            <h4 style="margin-bottom: 10px; font-size: 1.1em; color: #666;">${moduleName}</h4>
            <div class="permission-list" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 12px;">`;
        
        modules[moduleName].forEach(func => {
            const isChecked = rolePermissions.has(func.functionId);
            html += `
                <label class="permission-item" style="display: flex; align-items: flex-start; padding: 14px; border: 1px solid #ddd; border-radius: 6px; cursor: pointer; background: ${isChecked ? '#e8f5e9' : '#fff'}; transition: all 0.2s; min-width: 0;">
                    <input type="checkbox" 
                           class="permission-checkbox" 
                           data-function-id="${func.functionId}" 
                           data-function-code="${func.functionCode}"
                           ${isChecked ? 'checked' : ''}
                           style="margin-right: 12px; margin-top: 2px; flex-shrink: 0; width: 18px; height: 18px; cursor: pointer;">
                    <div style="flex: 1; min-width: 0; word-wrap: break-word; word-break: break-word; white-space: normal; line-height: 1.5;">
                        <div style="font-weight: 500; margin-bottom: 6px; color: #2c3e50; font-size: 14px;">${func.functionName}</div>
                        <div style="font-size: 0.85em; color: #666; line-height: 1.4;">${func.description || func.functionCode}</div>
                    </div>
                </label>
            `;
        });
        
        html += `</div></div>`;
    });
    
    html += '</div>';
    return html;
}

// 按模块分组功能
function groupFunctionsByModule(functions) {
    const modules = {};
    functions.forEach(func => {
        const moduleName = func.moduleName || '其他';
        if (!modules[moduleName]) {
            modules[moduleName] = [];
        }
        modules[moduleName].push(func);
    });
    return modules;
}

// 根据功能代码查找功能ID
function findFunctionIdByCode(functionCode) {
    const func = allFunctions.find(f => f.functionCode === functionCode);
    return func ? func.functionId : null;
}

// 全选权限
function selectAllPermissions() {
    const checkboxes = document.querySelectorAll('.permission-checkbox');
    checkboxes.forEach(cb => {
        cb.checked = true;
        updatePermissionItemStyle(cb);
    });
}

// 全不选权限
function deselectAllPermissions() {
    const checkboxes = document.querySelectorAll('.permission-checkbox');
    checkboxes.forEach(cb => {
        cb.checked = false;
        updatePermissionItemStyle(cb);
    });
}

// 更新权限项样式
function updatePermissionItemStyle(checkbox) {
    const item = checkbox.closest('.permission-item');
    if (item) {
        item.style.background = checkbox.checked ? '#e8f5e9' : '#fff';
    }
}

// 保存权限
async function savePermissions() {
    if (!currentRoleId) {
        showToast('请先选择角色', 'warning');
        return;
    }
    
    const checkboxes = document.querySelectorAll('.permission-checkbox:not(:disabled)');
    const selectedFunctionIds = [];
    const unselectedFunctionIds = [];
    
    checkboxes.forEach(cb => {
        const functionId = parseInt(cb.dataset.functionId);
        if (!isNaN(functionId) && functionId > 0) {
            if (cb.checked) {
                selectedFunctionIds.push(functionId);
            } else {
                // 如果之前有权限但现在取消了，需要撤销
                if (rolePermissions.has(functionId)) {
                    unselectedFunctionIds.push(functionId);
                }
            }
        }
    });
    
    if (selectedFunctionIds.length === 0 && unselectedFunctionIds.length === 0) {
        showToast('没有需要保存的权限变更', 'info');
        return;
    }
    
    try {
        // 先撤销取消的权限
        if (unselectedFunctionIds.length > 0) {
            await PermissionAPI.revokePermissions(currentRoleId, unselectedFunctionIds);
        }
        
        // 再分配新选择的权限（只分配新增的，已存在的不会重复分配）
        if (selectedFunctionIds.length > 0) {
            // 过滤掉已经存在的权限
            const newFunctionIds = selectedFunctionIds.filter(id => !rolePermissions.has(id));
            if (newFunctionIds.length > 0) {
                await PermissionAPI.assignPermissions(currentRoleId, newFunctionIds);
            }
        }
        
        showToast('权限保存成功！', 'success');
        
        // 重新加载权限数据以更新界面
        await loadPermissionData();
    } catch (error) {
        console.error('保存权限失败:', error);
        showToast('保存权限失败：' + (error.message || '未知错误'), 'error');
    }
}

// 重新加载权限分配页面
function loadPermissionAssignPage() {
    if (currentRoleId) {
        loadPermissionData();
    } else {
        initPermissionAssignPage();
    }
}

// 尝试应用待分配角色选择
function applyPendingPermissionRoleSelection() {
    if (!pendingPermissionRoleId) return false;
    
    const select = document.getElementById('permission-role-select');
    if (!select) return false;
    
    const targetValue = String(pendingPermissionRoleId);
    const hasOption = Array.from(select.options).some(opt => opt.value === targetValue);
    if (!hasOption) {
        return false;
    }
    
    select.value = targetValue;
    const roleId = pendingPermissionRoleId;
    pendingPermissionRoleId = null;
    onRoleSelected(targetValue);
    return true;
}

// 绑定权限复选框变化事件
document.addEventListener('change', function(e) {
    if (e.target.classList.contains('permission-checkbox')) {
        updatePermissionItemStyle(e.target);
    }
});

// ========== 学生选课功能 ==========

// 初始化学生选课页面
function initCourseSelectionPage() {
    // 确保样式正确应用
    const tabButtonsContainer = document.querySelector('#course-selection .tab-buttons');
    if (tabButtonsContainer) {
        // 确保容器样式
        tabButtonsContainer.style.display = 'flex';
        tabButtonsContainer.style.gap = '16px';
        tabButtonsContainer.style.marginBottom = '30px';
        tabButtonsContainer.style.padding = '8px';
        tabButtonsContainer.style.background = '#f8f9fa';
        tabButtonsContainer.style.borderRadius = '12px';
        tabButtonsContainer.style.boxShadow = 'inset 0 2px 4px rgba(0, 0, 0, 0.06)';
    }
    
    // 标签页切换
    const tabButtons = document.querySelectorAll('#course-selection .tab-btn');
    const tabPanes = document.querySelectorAll('#course-selection .tab-pane');
    
    // 确保按钮样式
    tabButtons.forEach(btn => {
        btn.style.display = 'flex';
        btn.style.alignItems = 'center';
        btn.style.justifyContent = 'center';
        btn.style.gap = '8px';
        btn.style.flex = '1';
        btn.style.padding = '16px 24px';
        btn.style.borderRadius = '8px';
        btn.style.border = 'none';
        btn.style.cursor = 'pointer';
        btn.style.fontSize = '15px';
        btn.style.fontWeight = '600';
        btn.style.transition = 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
        btn.style.position = 'relative';
        
        if (btn.classList.contains('active')) {
            btn.style.color = 'white';
            btn.style.background = 'linear-gradient(135deg, #3498db 0%, #2980b9 50%, #1abc9c 100%)';
            btn.style.boxShadow = '0 4px 15px rgba(52, 152, 219, 0.4), 0 2px 8px rgba(52, 152, 219, 0.3)';
            btn.style.transform = 'translateY(-2px)';
        } else {
            btn.style.color = '#6c757d';
            btn.style.background = 'transparent';
        }
    });

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            // 移除所有活动状态
            tabButtons.forEach(btn => {
                btn.classList.remove('active');
                btn.style.color = '#6c757d';
                btn.style.background = 'transparent';
                btn.style.boxShadow = 'none';
                btn.style.transform = 'translateY(0)';
            });
            tabPanes.forEach(pane => pane.classList.remove('active'));

            // 添加当前活动状态
            button.classList.add('active');
            button.style.color = 'white';
            button.style.background = 'linear-gradient(135deg, #3498db 0%, #2980b9 50%, #1abc9c 100%)';
            button.style.boxShadow = '0 4px 15px rgba(52, 152, 219, 0.4), 0 2px 8px rgba(52, 152, 219, 0.3)';
            button.style.transform = 'translateY(-2px)';
            
            const tabId = button.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');

            // 加载对应标签页的数据
            loadTabData(tabId);
        });
    });

    // 绑定按钮事件
    document.getElementById('refresh-available-courses')?.addEventListener('click', () => loadAvailableCourses());
    document.getElementById('refresh-my-courses')?.addEventListener('click', () => loadStudentSelectedCourses());

    // 初始化加载可选课程
    loadAvailableCourses();
}

// 加载标签页数据
function loadTabData(tabId) {
    switch(tabId) {
        case 'available-courses':
            loadAvailableCourses();
            break;
        case 'my-courses':
            loadStudentSelectedCourses();
            break;
    }
}

// 管理员测试专用学生ID（用于选课/考勤页面调试）
const ADMIN_TEST_STUDENT_ID = 1;

// 加载可选课程
async function loadAvailableCourses() {
    const container = document.getElementById('available-courses-list');
    if (!container) return;

    const currentUser = getCurrentUser();
    const isStudent = currentUser && currentUser.roleId === 3;
    const isAdmin = currentUser && currentUser.roleId === 1;

    if (!isStudent && !isAdmin) {
        container.innerHTML = '<div style="text-align:center;padding:40px;color:var(--danger-color);">仅学生/管理员测试可访问</div>';
        showToast('仅学生/管理员测试可访问', 'warning');
        return;
    }

    container.innerHTML = '<div class="loading">正在加载...</div>';

    const studentId = isAdmin ? ADMIN_TEST_STUDENT_ID : await getCurrentStudentId();
    if (!studentId) {
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--danger-color);">无法获取当前学生信息，无法加载可选课程</div>';
        showToast('无法获取当前学生信息', 'error');
        return;
    }

    if (isAdmin) {
        container.innerHTML = '<div style="margin-bottom:10px;color:#6B7280;">管理员测试模式：使用学生ID ' + studentId + '</div>' + container.innerHTML;
    }
    
    StudentCourseAPI.getAvailableCourses(studentId)
        .then(courses => {
            if (!courses || courses.length === 0) {
                container.innerHTML = `
                    <div style="text-align: center; padding: 60px 20px; color: var(--text-light); background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);">
                        <div style="font-size: 48px; margin-bottom: 16px;">📚</div>
                        <div style="font-size: 16px; margin-bottom: 8px; font-weight: 600;">暂无可选课程</div>
                        <div style="font-size: 14px; color: #95a5a6;">请稍后再试或联系管理员</div>
                    </div>
                `;
                // 更新计数
                const countElement = document.getElementById('available-count');
                if (countElement) {
                    countElement.textContent = '0';
                }
                return;
            }

            container.innerHTML = `
                <table class="course-table">
                    <thead>
                        <tr>
                            <th>课程名称</th>
                            <th>课程代码</th>
                            <th>班级</th>
                            <th>学分</th>
                            <th>上课时间</th>
                            <th>上课地点</th>
                            <th>学期</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${courses.map(course => `
                            <tr>
                                <td class="course-name-cell">${course.courseName || '未知'}</td>
                                <td class="course-info-cell">${course.courseCode || 'N/A'}</td>
                                <td class="course-info-cell">${course.className || '未知'}</td>
                                <td class="course-info-cell">${course.credits || '0'}</td>
                                <td class="course-info-cell">${course.classTime || '未设置'}</td>
                                <td class="course-info-cell">${course.classLocation || '未设置'}</td>
                                <td class="course-info-cell">${course.semester || '未知'}</td>
                                <td class="course-actions-cell">
                                    <button class="btn btn-sm btn-accent" onclick="enrollCourse(${course.classId})">选课</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;

            // 更新计数
            const countElement = document.getElementById('available-count');
            if (countElement) {
                countElement.textContent = courses.length;
            }
        })
        .catch(error => {
            console.error('加载可选课程失败:', error);
            container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--danger-color);">加载失败，请稍后重试</div>';
            showToast('加载可选课程失败', 'error');
        });
}

// 加载我的课程（学生已选）
async function loadStudentSelectedCourses() {
    const container = document.getElementById('my-courses-list');
    if (!container) return;

    const currentUser = getCurrentUser();
    const isStudent = currentUser && currentUser.roleId === 3;
    const isAdmin = currentUser && currentUser.roleId === 1;

    if (!isStudent && !isAdmin) {
        container.innerHTML = '<div style="text-align:center;padding:40px;color:var(--danger-color);">仅学生/管理员测试可访问</div>';
        showToast('仅学生/管理员测试可访问', 'warning');
        return;
    }

    container.innerHTML = '<div class="loading">正在加载...</div>';

    const studentId = isAdmin ? ADMIN_TEST_STUDENT_ID : await getCurrentStudentId();
    if (!studentId) {
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--danger-color);">无法获取当前学生信息，无法加载已选课程</div>';
        showToast('无法获取当前学生信息', 'error');
        return;
    }

    if (isAdmin) {
        container.innerHTML = '<div style="margin-bottom:10px;color:#6B7280;">管理员测试模式：使用学生ID ' + studentId + '</div>' + container.innerHTML;
    }

    // 调用后端API获取已选课程
    StudentCourseAPI.getStudentCourses(studentId)
        .then(courses => {
            if (!courses || courses.length === 0) {
                container.innerHTML = `
                    <div style="text-align: center; padding: 60px 20px; color: var(--text-light); background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);">
                        <div style="font-size: 48px; margin-bottom: 16px;">📖</div>
                        <div style="font-size: 16px; margin-bottom: 8px; font-weight: 600;">暂无已选课程</div>
                        <div style="font-size: 14px; color: #95a5a6;">快去选择你感兴趣的课程吧！</div>
                    </div>
                `;
                // 更新计数
                const countElement = document.getElementById('my-courses-count');
                if (countElement) {
                    countElement.textContent = '0';
                }
                return;
            }

            container.innerHTML = `
                <table class="course-table">
                    <thead>
                        <tr>
                            <th>课程名称</th>
                            <th>课程代码</th>
                            <th>班级</th>
                            <th>学分</th>
                            <th>上课时间</th>
                            <th>上课地点</th>
                            <th>学期</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${courses.map(course => `
                            <tr>
                                <td class="course-name-cell">${course.courseName || '未知'}</td>
                                <td class="course-info-cell">${course.courseCode || 'N/A'}</td>
                                <td class="course-info-cell">${course.className || '未知'}</td>
                                <td class="course-info-cell">${course.credits || '0'}</td>
                                <td class="course-info-cell">${course.classTime || '未设置'}</td>
                                <td class="course-info-cell">${course.classLocation || '未设置'}</td>
                                <td class="course-info-cell">${course.semester || '未知'}</td>
                                <td><span class="status-enrolled">${getStatusText('ENROLLED')}</span></td>
                                <td class="course-actions-cell">
                                    <button class="btn btn-sm btn-danger" onclick="dropCourse(${course.classId})">退课</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;

            // 更新计数
            const countElement = document.getElementById('my-courses-count');
            if (countElement) {
                countElement.textContent = courses.length;
            }
        })
        .catch(error => {
            console.error('加载已选课程失败:', error);
            container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--danger-color);">加载失败，请稍后重试</div>';
            showToast('加载已选课程失败', 'error');
        });
}

// 单个选课
async function enrollCourse(classId) {
    if (!confirm('确认要选修这门课程吗？')) {
        return;
    }

    const studentId = await getCurrentStudentId();
    if (!studentId) {
        showToast('无法获取当前学生信息，选课失败', 'error');
        return;
    }

    // 调用后端API进行选课
    StudentCourseAPI.enroll(studentId, classId)
        .then(result => {
            showToast(`课程 ${classId} 选修成功`, 'success');
            // 刷新数据
            loadAvailableCourses();
            loadStudentSelectedCourses();
        })
        .catch(error => {
            console.error('选课失败:', error);
            showToast(`课程 ${classId} 选修失败`, 'error');
        });
}

// 退课
async function dropCourse(classId) {
    if (!confirm('确认要退修这门课程吗？此操作不可撤销。')) {
        return;
    }

    const studentId = await getCurrentStudentId();
    if (!studentId) {
        showToast('无法获取当前学生信息，退课失败', 'error');
        return;
    }

    // 调用后端API进行退课
    StudentCourseAPI.drop(studentId, classId)
        .then(result => {
            showToast(`课程 ${classId} 退修成功`, 'success');
            // 刷新数据
            loadAvailableCourses();
            loadStudentSelectedCourses();
        })
        .catch(error => {
            console.error('退课失败:', error);
            showToast(`课程 ${classId} 退修失败`, 'error');
        });
}

// 查看课程详情
function checkCourseDetails(classId) {
    // 这里可以实现查看课程详细信息的弹窗
    showToast(`查看课程 ${classId} 详情`, 'info');
}

// 获取状态文本
function getStatusText(status) {
    switch(status) {
        case 'ENROLLED': return '已选修';
        case 'DROPPED': return '已退修';
        case 'COMPLETED': return '已完成';
        default: return status;
    }
}

// 管理员查询学生选课
async function adminQueryStudentCourses() {
    const numberInput = document.getElementById('admin-course-query-number');
    const idInput = document.getElementById('admin-course-query-studentid');
    const resultDiv = document.getElementById('admin-course-query-result');

    const currentUser = getCurrentUser();
    if (!currentUser || currentUser.roleId !== 1) {
        resultDiv.innerHTML = '<div style="text-align:center;padding:20px;color:var(--danger-color);">仅管理员可用</div>';
        showToast('仅管理员可用', 'warning');
        return;
    }

    const studentNumber = numberInput?.value?.trim();
    const studentIdInput = idInput?.value ? parseInt(idInput.value, 10) : null;

    resultDiv.innerHTML = '<div style="text-align:center;padding:20px;">查询中...</div>';

    try {
        let studentId = studentIdInput;
        let studentInfo = null;

        if (!studentId) {
            if (!studentNumber) {
                resultDiv.innerHTML = '<div style="text-align:center;padding:20px;color:var(--danger-color);">请先输入学号或学生ID</div>';
                return;
            }
            studentInfo = await AdminCourseViewAPI.getStudentByNumber(studentNumber);
            studentId = studentInfo?.studentId;
            if (!studentId) {
                resultDiv.innerHTML = '<div style="text-align:center;padding:20px;color:var(--danger-color);">未找到该学号的学生</div>';
                return;
            }
        } else {
            // 如果提供ID但未提供学号，尝试补全学号/姓名（忽略失败）
            if (studentNumber) {
                studentInfo = await AdminCourseViewAPI.getStudentByNumber(studentNumber).catch(() => null);
            }
        }

        const courses = await AdminCourseViewAPI.getStudentCoursesByStudentId(studentId);
        const rows = [];
        const infoName = studentInfo?.studentName || studentInfo?.name || '未知';
        const infoNumber = studentInfo?.studentNumber || studentNumber || '未知';

        if (!courses || courses.length === 0) {
            rows.push({
                studentNumber: infoNumber,
                studentName: infoName,
                courseName: '-',
                courseCode: '-',
                className: '-'
            });
        } else {
            courses.forEach(c => {
                rows.push({
                    studentNumber: infoNumber,
                    studentName: infoName,
                    courseName: c.courseName || '-',
                    courseCode: c.courseCode || '-',
                    className: c.className || '-'
                });
            });
        }

        adminCourseViewState.rows = rows;
        adminCourseViewState.page = 0;
        renderAdminCourseViewTable();
    } catch (err) {
        console.error('管理员查询学生选课失败:', err);
        resultDiv.innerHTML = '<div style="text-align:center;padding:20px;color:var(--danger-color);">查询失败：' + (err.message || '未知错误') + '</div>';
    }
}

// 管理员加载全部学生选课（按学生合并展示）
async function loadAdminStudentCoursesAll() {
    const resultDiv = document.getElementById('admin-course-query-result');
    const currentUser = getCurrentUser();
    if (!currentUser || currentUser.roleId !== 1) {
        resultDiv.innerHTML = '<div style="text-align:center;padding:20px;color:var(--danger-color);">仅管理员可用</div>';
        showToast('仅管理员可用', 'warning');
        return;
    }

    resultDiv.innerHTML = '<div style="text-align:center;padding:20px;">加载全部学生选课...</div>';

    try {
        const students = await StudentAPI.getAll();
        if (!students || students.length === 0) {
            resultDiv.innerHTML = '<div style="text-align:center;padding:20px;">暂无学生数据</div>';
            return;
        }

        // 按学生并发获取选课
        const list = await Promise.all(students.map(async s => {
            try {
                const courses = await AdminCourseViewAPI.getStudentCoursesByStudentId(s.studentId);
                return { student: s, courses: courses || [] };
            } catch (e) {
                return { student: s, courses: [], error: true };
            }
        }));

        const rows = [];
        list.forEach(item => {
            if (!item.courses || item.courses.length === 0) {
                rows.push({
                    studentNumber: item.student.studentNumber || '-',
                    studentName: item.student.studentName || '-',
                    courseName: '-',
                    courseCode: '-',
                    className: '-'
                });
            } else {
                item.courses.forEach(c => {
                    rows.push({
                        studentNumber: item.student.studentNumber || '-',
                        studentName: item.student.studentName || '-',
                        courseName: c.courseName || '-',
                        courseCode: c.courseCode || '-',
                        className: c.className || '-'
                    });
                });
            }
        });

        adminCourseViewState.rows = rows;
        adminCourseViewState.page = 0;
        renderAdminCourseViewTable();
    } catch (err) {
        console.error('管理员加载全部学生选课失败:', err);
        resultDiv.innerHTML = '<div style="text-align:center;padding:20px;color:var(--danger-color);">加载失败：' + (err.message || '未知错误') + '</div>';
    }
}

// 学生选课查看分页状态
let adminCourseViewState = { rows: [], page: 0, size: 10, totalPages: 1 };

function renderAdminCourseViewTable() {
    const resultDiv = document.getElementById('admin-course-query-result');
    if (!resultDiv) return;
    const pageSelect = document.getElementById('admin-course-page-size');
    const currentPageSpan = document.getElementById('admin-course-page-current');
    const totalPageSpan = document.getElementById('admin-course-page-total');
    const btnPrev = document.getElementById('admin-course-page-prev');
    const btnNext = document.getElementById('admin-course-page-next');

    if (pageSelect) {
        adminCourseViewState.size = parseInt(pageSelect.value, 10) || 10;
    }

    const rows = adminCourseViewState.rows || [];
    adminCourseViewState.totalPages = Math.max(1, Math.ceil(rows.length / adminCourseViewState.size));
    if (adminCourseViewState.page >= adminCourseViewState.totalPages) {
        adminCourseViewState.page = adminCourseViewState.totalPages - 1;
    }
    const start = adminCourseViewState.page * adminCourseViewState.size;
    const end = start + adminCourseViewState.size;
    const slice = rows.slice(start, end);

    if (!rows.length) {
        resultDiv.innerHTML = '<div style="text-align:center;padding:20px;">暂无选课记录</div>';
    } else {
        resultDiv.innerHTML = `
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>学号</th>
                            <th>姓名</th>
                            <th>课程名称</th>
                            <th>课程代码</th>
                            <th>班级</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${slice.map(r => `
                            <tr>
                                <td>${r.studentNumber}</td>
                                <td>${r.studentName}</td>
                                <td>${r.courseName}</td>
                                <td>${r.courseCode}</td>
                                <td>${r.className}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }

    if (currentPageSpan) currentPageSpan.textContent = adminCourseViewState.page + 1;
    if (totalPageSpan) totalPageSpan.textContent = adminCourseViewState.totalPages;
    if (btnPrev) btnPrev.disabled = adminCourseViewState.page <= 0;
    if (btnNext) btnNext.disabled = adminCourseViewState.page >= adminCourseViewState.totalPages - 1;
}

// ========== 考勤统计功能 ==========

// 初始化考勤统计页面
function initStatisticsPage() {
    const generateBtn = document.querySelector('#statistics button.btn-accent');
    const classSelect = document.querySelector('#statistics select');

    // 加载班级选项
    loadClassOptions();

    // 生成统计报告按钮事件
    if (generateBtn) {
        generateBtn.addEventListener('click', generateStatisticsReport);
    }

    function loadClassOptions() {
        CourseClassAPI.getAll().then(classes => {
            if (!classes || classes.length === 0) {
                classSelect.innerHTML = '<option value="">暂无班级数据</option>';
                return;
            }
            classSelect.innerHTML = '<option value="">请选择班级</option>' +
                classes.map(c => `<option value="${c.classId}">${c.className} (ID:${c.classId})</option>`).join('');
        }).catch(err => {
            console.error('加载班级失败:', err);
            classSelect.innerHTML = '<option value="">加载失败</option>';
            showToast('加载班级失败，请检查网络或后端服务', 'error');
        });
    }

    function generateStatisticsReport() {
        const classId = classSelect.value;
        if (!classId) {
            showToast('请选择班级', 'warning');
            return;
        }

        // 显示加载状态
        generateBtn.disabled = true;
        generateBtn.textContent = '生成中...';

        // 调用考勤任务统计API
        fetch(`/api/attendance-tasks/class/${classId}/statistics`)
            .then(response => response.json())
            .then(data => {
                displayStatisticsReport(data, classId);
                showToast('统计报告生成成功', 'success');
            })
            .catch(error => {
                console.error('生成统计报告失败:', error);
                showToast('生成统计报告失败，请稍后重试', 'error');
            })
            .finally(() => {
                generateBtn.disabled = false;
                generateBtn.textContent = '生成统计报告';
            });
    }

    function displayStatisticsReport(statistics, classId) {
        const resultDiv = document.querySelector('#statistics .card-body > div:last-child');
        if (!resultDiv) return;

        const html = `
            <h3>考勤统计结果</h3>
            <div class="statistics-summary" style="margin: 20px 0; padding: 15px; background: #f8f9fa; border-radius: 8px;">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px;">
                    <div class="stat-item">
                        <strong>总任务数:</strong> ${statistics.totalTasks || 0}
                    </div>
                    <div class="stat-item">
                        <strong>活跃任务:</strong> ${statistics.activeTasks || 0}
                    </div>
                    <div class="stat-item">
                        <strong>已过期任务:</strong> ${statistics.expiredTasks || 0}
                    </div>
                    <div class="stat-item">
                        <strong>即将开始:</strong> ${statistics.upcomingTasks || 0}
                    </div>
                </div>
            </div>

            <div class="table-container" style="margin-top: 15px;">
                <table>
                    <thead>
                        <tr>
                            <th>任务ID</th>
                            <th>开始时间</th>
                            <th>结束时间</th>
                            <th>状态</th>
                            <th>位置范围</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="statistics-table-body">
                        <!-- 动态填充任务详情 -->
                    </tbody>
                </table>
            </div>
        `;

        resultDiv.innerHTML = html;

        // 加载并显示任务详情
        loadTaskDetails(classId);
    }

    function loadTaskDetails(classId) {
        AttendanceTaskAPI.getByCourseClassId(classId)
            .then(tasks => {
                const tbody = document.getElementById('statistics-table-body');
                if (!tbody) return;

                if (!tasks || tasks.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">暂无考勤任务数据</td></tr>';
                    return;
                }

                tbody.innerHTML = tasks.map(task => {
                    const status = getTaskStatus(task);
                    return `
                        <tr>
                            <td>${task.taskId}</td>
                            <td>${formatDateTime(task.startTime)}</td>
                            <td>${formatDateTime(task.endTime)}</td>
                            <td><span class="status-${status.toLowerCase()}">${status}</span></td>
                            <td>${task.locationRange || '未设置'}</td>
                            <td>
                                <button class="btn btn-sm btn-accent" onclick="viewAttendanceStatus(${task.taskId})">
                                    查看考勤状况
                                </button>
                            </td>
                        </tr>
                    `;
                }).join('');
            })
            .catch(error => {
                console.error('加载任务详情失败:', error);
                const tbody = document.getElementById('statistics-table-body');
                if (tbody) {
                    tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: red;">加载失败</td></tr>';
                }
            });
    }

    function getTaskStatus(task) {
        const now = new Date();
        const startTime = new Date(task.startTime);
        const endTime = new Date(task.endTime);

        if (now < startTime) return 'UPCOMING';
        if (now > endTime) return 'EXPIRED';
        return 'ACTIVE';
    }

    function formatDateTime(dateTimeStr) {
        const date = new Date(dateTimeStr);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
}

// 格式化日期时间（全局函数）
function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '-';
    const date = new Date(dateTimeStr);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 查看班级考勤状况
function viewAttendanceStatus(taskId) {
    const container = document.getElementById('attendance-status-container');
    const contentDiv = document.getElementById('attendance-status-content');
    
    if (!container || !contentDiv) {
        showToast('页面元素未找到，请刷新页面重试', 'error');
        return;
    }

    // 显示加载状态
    container.style.display = 'block';
    contentDiv.innerHTML = '<div style="text-align: center; padding: 40px;">加载中...</div>';
    
    // 滚动到考勤状况区域
    container.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

    // 调用API获取考勤状况
    AttendanceTaskAPI.getClassAttendanceStatus(taskId)
        .then(statusList => {
            if (!statusList || statusList.length === 0) {
                contentDiv.innerHTML = '<div style="text-align: center; padding: 40px; color: #666;">暂无考勤数据</div>';
                return;
            }

            // 统计考勤情况
            const total = statusList.length;
            const checkedIn = statusList.filter(s => s.attendanceResult !== '未签到').length;
            const notCheckedIn = total - checkedIn;
            const attendanceRate = total > 0 ? ((checkedIn / total) * 100).toFixed(1) : 0;

            // 按考勤状态分组统计
            const statusCounts = {};
            statusList.forEach(s => {
                const status = s.attendanceResult || '未签到';
                statusCounts[status] = (statusCounts[status] || 0) + 1;
            });

            // 构建统计信息HTML
            const statsHtml = `
                <div style="margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px;">
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 20px; margin-bottom: 15px;">
                        <div style="font-size: 1.1em;">
                            <strong>总人数:</strong> <span style="color: #333; font-size: 1.2em;">${total}</span>
                        </div>
                        <div style="font-size: 1.1em;">
                            <strong>已签到:</strong> <span style="color: #28a745; font-size: 1.2em;">${checkedIn}</span>
                        </div>
                        <div style="font-size: 1.1em;">
                            <strong>未签到:</strong> <span style="color: #6c757d; font-size: 1.2em;">${notCheckedIn}</span>
                        </div>
                        <div style="font-size: 1.1em;">
                            <strong>出勤率:</strong> <span style="color: #007bff; font-size: 1.2em;">${attendanceRate}%</span>
                        </div>
                    </div>
                    <div style="display: flex; flex-wrap: wrap; gap: 10px; margin-top: 10px;">
                        ${Object.entries(statusCounts).map(([status, count]) => 
                            `<span style="padding: 8px 15px; background: ${getStatusColor(status)}; color: white; border-radius: 4px; font-size: 0.95em;">
                                <strong>${status}:</strong> ${count}
                            </span>`
                        ).join('')}
                    </div>
                </div>
            `;

            // 构建表格HTML
            const tableHtml = `
                ${statsHtml}
                <div class="table-container" style="max-height: 600px; overflow-y: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>学号</th>
                                <th>姓名</th>
                                <th>考勤状态</th>
                                <th>签到时间</th>
                                <th>备注</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${statusList.map(student => `
                                <tr>
                                    <td>${student.studentNumber || 'N/A'}</td>
                                    <td>${student.studentName || 'N/A'}</td>
                                    <td>
                                        <span style="padding: 4px 10px; border-radius: 4px; background: ${getStatusColor(student.attendanceResult)}; color: white; font-size: 0.9em;">
                                            ${student.attendanceResult || '未签到'}
                                        </span>
                                    </td>
                                    <td>${student.checkInTime ? formatDateTime(student.checkInTime) : '-'}</td>
                                    <td>${student.remark || '-'}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;

            contentDiv.innerHTML = tableHtml;
            
            // 再次滚动到考勤状况区域，确保用户能看到
            setTimeout(() => {
                container.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 100);
        })
        .catch(error => {
            console.error('加载考勤状况失败:', error);
            contentDiv.innerHTML = `<div style="text-align: center; padding: 40px; color: #dc3545;">
                <p style="font-size: 1.1em; margin-bottom: 10px;">加载失败</p>
                <p style="color: #666; font-size: 0.9em;">${error.message || '未知错误'}</p>
            </div>`;
        });
}

// 关闭考勤状况显示
function closeAttendanceStatus() {
    const container = document.getElementById('attendance-status-container');
    if (container) {
        container.style.display = 'none';
    }
}

// 获取考勤状态对应的颜色
function getStatusColor(status) {
    const colorMap = {
        '未签到': '#6c757d',
        '正常': '#28a745',
        '迟到': '#ffc107',
        '早退': '#fd7e14',
        '缺勤': '#dc3545',
        '请假': '#17a2b8'
    };
    return colorMap[status] || '#6c757d';
}

// ========== 人脸数据录入 ========== 
const faceEntryState = {
    stream: null,
    capturedData: null
};

function initFaceDataEntryPage() {
    const captureBtn = document.getElementById('btn-face-capture');
    const uploadTrigger = document.getElementById('btn-face-upload-trigger');
    const stopCameraBtn = document.getElementById('btn-face-stop-camera');
    const fileInput = document.getElementById('face-file-input');
    const submitBtn = document.getElementById('btn-face-submit');
    const urlInput = document.getElementById('face-url-input');
    const urlLoadBtn = document.getElementById('btn-face-url-load');
    const videoEl = document.getElementById('face-video');
    const previewImg = document.getElementById('face-preview');
    const placeholder = document.getElementById('face-preview-placeholder');
    const statusText = document.getElementById('face-status-text');

    if (!captureBtn || !uploadTrigger || !fileInput || !submitBtn || !videoEl) {
        console.warn('人脸录入：页面元素未加载完成');
        return;
    }

    captureBtn.onclick = () => captureFaceSnapshot(videoEl, previewImg, placeholder, statusText);
    uploadTrigger.onclick = () => fileInput.click();
    if (stopCameraBtn) {
        stopCameraBtn.onclick = () => stopFaceCamera(statusText);
    }
    fileInput.onchange = (e) => handleFaceFileUpload(e.target.files, previewImg, placeholder, statusText);
    submitBtn.onclick = () => submitFaceData(statusText);
    if (urlLoadBtn) {
        urlLoadBtn.onclick = () => {
            const url = urlInput ? urlInput.value.trim() : '';
            if (!url) {
                showToast('请填写图片链接', 'warning');
                return;
            }
            loadImageFromUrl(url, (dataUrl) => {
                setFacePreview(dataUrl, previewImg, placeholder);
                if (statusText) statusText.textContent = '已从链接加载';
            }, (msg) => {
                showToast(msg || '图片加载失败', 'error');
                if (statusText) statusText.textContent = '链接加载失败';
            }, statusText);
        };
    }

    // 页面初始化时尝试加载已有的人脸数据
    loadExistingFaceData(previewImg, placeholder, statusText);
}

async function startFaceCamera(videoEl, statusText) {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        showToast('当前浏览器不支持摄像头访问，请改用上传照片', 'warning');
        return;
    }

    try {
        stopFaceCamera();
        const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'user' } });
        videoEl.srcObject = stream;
        faceEntryState.stream = stream;
        if (statusText) statusText.textContent = '摄像头已开启';
    } catch (err) {
        console.error('打开摄像头失败：', err);
        let errorMsg = '无法访问摄像头';
        
        if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
            errorMsg = '摄像头权限被拒绝，请在浏览器设置中允许访问摄像头';
        } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
            errorMsg = '未找到摄像头设备，请检查设备连接';
        } else if (err.name === 'NotReadableError' || err.name === 'TrackStartError') {
            errorMsg = '摄像头被占用，请关闭其他使用摄像头的程序';
        } else if (err.name === 'OverconstrainedError' || err.name === 'ConstraintNotSatisfiedError') {
            errorMsg = '摄像头不支持所需配置';
        } else if (err.name === 'TypeError') {
            errorMsg = '摄像头参数错误';
        } else if (err.name === 'SecurityError') {
            errorMsg = '安全限制：请使用 HTTPS 或 localhost 访问';
        }
        
        showToast(errorMsg, 'error');
        if (statusText) statusText.textContent = '摄像头未开启';
    }
}

function stopFaceCamera(statusText) {
    if (faceEntryState.stream) {
        faceEntryState.stream.getTracks().forEach(track => track.stop());
        faceEntryState.stream = null;
    }
    if (statusText) statusText.textContent = '摄像头已关闭';
}

async function captureFaceSnapshot(videoEl, previewImg, placeholder, statusText) {
    // 若未开启摄像头，点击拍照时自动申请权限并开启
    if (!faceEntryState.stream) {
        if (statusText) statusText.textContent = '正在请求摄像头权限...';
        await startFaceCamera(videoEl, statusText);
        if (!faceEntryState.stream) {
            return;
        }
        await waitVideoReady(videoEl);
    }

    const canvas = document.getElementById('face-canvas');
    if (!canvas) return;

    canvas.width = videoEl.videoWidth || 640;
    canvas.height = videoEl.videoHeight || 480;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(videoEl, 0, 0, canvas.width, canvas.height);
    const dataUrl = canvas.toDataURL('image/jpeg', 0.9);

    setFacePreview(dataUrl, previewImg, placeholder);
    if (statusText) statusText.textContent = '已从摄像头采集照片';
}

function waitVideoReady(videoEl) {
    return new Promise((resolve) => {
        if (videoEl.readyState >= 2) {
            resolve();
            return;
        }
        const onLoaded = () => {
            videoEl.removeEventListener('loadeddata', onLoaded);
            resolve();
        };
        videoEl.addEventListener('loadeddata', onLoaded);
        setTimeout(resolve, 800);
    });
}

function handleFaceFileUpload(files, previewImg, placeholder, statusText) {
    if (!files || files.length === 0) return;
    const file = files[0];
    if (!file.type.startsWith('image/')) {
        showToast('请上传图片文件', 'warning');
        return;
    }

    const reader = new FileReader();
    reader.onload = () => {
        setFacePreview(reader.result, previewImg, placeholder);
        if (statusText) statusText.textContent = '已选择上传的照片';
    };
    reader.readAsDataURL(file);
}

function setFacePreview(dataUrl, previewImg, placeholder) {
    faceEntryState.capturedData = dataUrl;
    if (previewImg) {
        previewImg.src = dataUrl;
        previewImg.style.display = 'block';
    }
    if (placeholder) {
        placeholder.style.display = 'none';
    }
}

// 通用：从 URL 拉取图片并转为 DataURL
function loadImageFromUrl(url, onSuccess, onError, statusText) {
    if (statusText) statusText.textContent = '正在加载图片...';
    fetch(url, { mode: 'cors' })
        .then(resp => {
            if (!resp.ok) throw new Error(`图片请求失败，状态码 ${resp.status}`);
            return resp.blob();
        })
        .then(blob => {
            if (!blob.type.startsWith('image/')) {
                throw new Error('链接内容不是图片');
            }
            const reader = new FileReader();
            reader.onload = () => {
                if (onSuccess) onSuccess(reader.result);
            };
            reader.onerror = () => {
                if (onError) onError('图片读取失败');
            };
            reader.readAsDataURL(blob);
        })
        .catch(err => {
            console.error('加载图片失败', err);
            if (onError) onError(err.message || '加载图片失败');
        })
        .finally(() => {
            if (statusText) statusText.textContent = '';
        });
}

async function loadExistingFaceData(previewImg, placeholder, statusText) {
    const user = getCurrentUser();
    if (!user) return;

    try {
        const data = await FaceDataAPI.getMine();
        if (data && data.faceTemplate) {
            setFacePreview(data.faceTemplate, previewImg, placeholder);
            if (statusText) statusText.textContent = '已加载历史人脸数据，可重新覆盖';
        }
    } catch (err) {
        console.warn('加载历史人脸数据失败：', err.message || err);
    }
}

async function submitFaceData(statusText) {
    const user = getCurrentUser();
    if (!user) {
        showToast('请先登录后再提交人脸数据', 'error');
        return;
    }

    if (!faceEntryState.capturedData) {
        showToast('请先拍照或上传照片', 'warning');
        return;
    }

    if (statusText) statusText.textContent = '正在保存...';
    try {
        await FaceDataAPI.save({
            userId: user.userId,
            faceImage: faceEntryState.capturedData
        });
        showToast('人脸数据已保存', 'success');
        if (statusText) statusText.textContent = '保存成功，可重新采集覆盖';
    } catch (err) {
        console.error('保存人脸数据失败：', err);
        showToast(err.message || '保存人脸数据失败', 'error');
        if (statusText) statusText.textContent = '保存失败，请重试';
    }
}

// ========== 人脸识别测试 ==========
const faceTestState = {
    dataUrl: null
};

function initFaceTestPage() {
    const fileInput = document.getElementById('face-test-file');
    const uploadBtn = document.getElementById('btn-face-test-upload');
    const clearBtn = document.getElementById('btn-face-test-clear');
    const runBtn = document.getElementById('btn-face-test-run');
    const urlInput = document.getElementById('face-test-url-input');
    const urlLoadBtn = document.getElementById('btn-face-test-url-load');
    const preview = document.getElementById('face-test-preview');
    const placeholder = document.getElementById('face-test-placeholder');
    const statusText = document.getElementById('face-test-status');
    const resultBox = document.getElementById('face-test-result');
    const dropZone = preview?.parentElement;

    if (!fileInput || !uploadBtn || !clearBtn || !runBtn) {
        console.warn('人脸识别测试：页面元素未加载完成');
        return;
    }

    uploadBtn.onclick = () => fileInput.click();
    clearBtn.onclick = () => {
        faceTestState.dataUrl = null;
        if (preview) {
            preview.src = '';
            preview.style.display = 'none';
        }
        if (placeholder) placeholder.style.display = 'block';
        if (statusText) statusText.textContent = '已清除';
        if (resultBox) resultBox.textContent = '尚未开始测试';
    };
    fileInput.onchange = (e) => handleFaceTestFile(e.target.files, preview, placeholder, statusText);
    runBtn.onclick = () => runFaceTest(statusText, resultBox);
    if (urlLoadBtn) {
        urlLoadBtn.onclick = () => {
            const url = urlInput ? urlInput.value.trim() : '';
            if (!url) {
                showToast('请填写图片链接', 'warning');
                return;
            }
            loadImageFromUrl(url, (dataUrl) => {
                faceTestState.dataUrl = dataUrl;
                if (preview) {
                    preview.src = dataUrl;
                    preview.style.display = 'block';
                }
                if (placeholder) placeholder.style.display = 'none';
                if (statusText) statusText.textContent = '已从链接加载';
            }, (msg) => {
                showToast(msg || '图片加载失败', 'error');
                if (statusText) statusText.textContent = '链接加载失败';
            }, statusText);
        };
    }

    // 支持拖拽上传
    if (dropZone) {
        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.style.borderColor = '#409eff';
        });
        dropZone.addEventListener('dragleave', () => {
            dropZone.style.borderColor = '#dfe4ea';
        });
        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.style.borderColor = '#dfe4ea';
            if (e.dataTransfer?.files?.length) {
                handleFaceTestFile(e.dataTransfer.files, preview, placeholder, statusText);
            }
        });
    }
}

function handleFaceTestFile(files, preview, placeholder, statusText) {
    if (!files || files.length === 0) return;
    const file = files[0];
    if (!file.type.startsWith('image/')) {
        showToast('请上传图片文件', 'warning');
        return;
    }

    const reader = new FileReader();
    reader.onload = () => {
        faceTestState.dataUrl = reader.result;
        if (preview) {
            preview.src = reader.result;
            preview.style.display = 'block';
        }
        if (placeholder) placeholder.style.display = 'none';
        if (statusText) statusText.textContent = '已选择图片';
    };
    reader.readAsDataURL(file);
}

function dataUrlToBlob(dataUrl) {
    const arr = dataUrl.split(',');
    const mime = arr[0].match(/:(.*?);/)[1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new Blob([u8arr], { type: mime });
}

async function runFaceTest(statusText, resultBox) {
    if (!faceTestState.dataUrl) {
        showToast('请先选择图片', 'warning');
        return;
    }

    if (statusText) statusText.textContent = '调用识别服务中...';
    if (resultBox) resultBox.textContent = '请求中...';

    try {
        const data = await FaceRecognitionAPI.recognize(faceTestState.dataUrl);
        if (!data) throw new Error('服务无响应');

        const lines = [];
        lines.push(`has_face: ${data.hasFace}`);
        lines.push(`matched: ${data.matched}`);
        if (data.similarity !== undefined) lines.push(`similarity: ${Number(data.similarity).toFixed(4)}`);
        if (data.threshold !== undefined) lines.push(`threshold: ${Number(data.threshold).toFixed(4)}`);
        if (data.userId) lines.push(`userId: ${data.userId}`);
        if (data.userName) lines.push(`userName: ${data.userName}`);
        if (data.message) lines.push(`message: ${data.message}`);

        if (resultBox) resultBox.textContent = lines.join('\n');

        if (data.hasFace && data.matched) {
            if (statusText) statusText.textContent = '匹配成功';
            showToast('匹配成功', 'success');
        } else if (data.hasFace && !data.matched) {
            if (statusText) statusText.textContent = '未匹配到库内人脸';
            showToast(data.message || '未匹配到库内人脸', 'warning');
        } else {
            if (statusText) statusText.textContent = '未检测到人脸';
            showToast(data.message || '未检测到人脸', 'warning');
        }
    } catch (err) {
        console.error('人脸识别测试失败', err);
        if (statusText) statusText.textContent = '调用失败';
        if (resultBox) resultBox.textContent = err.message || '调用失败';
        showToast(err.message || '识别失败', 'error');
    }
}
