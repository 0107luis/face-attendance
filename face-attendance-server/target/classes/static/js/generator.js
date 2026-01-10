/**
 * 自动生成工具类
 * 用于生成学号、工号、用户名、密码等
 */

const Generator = {
    /**
     * 生成学号
     * 格式：年份(4位) + 流水号(4位)
     * 例如：20240001
     */
    generateStudentNumber: function() {
        const year = new Date().getFullYear();
        const sequence = Math.floor(Math.random() * 9000) + 1; // 0001-9999
        return `${year}${String(sequence).padStart(4, '0')}`;
    },

    /**
     * 生成教师工号
     * 格式：T + 年份后2位 + 流水号(5位)
     * 例如：T24000001
     */
    generateTeacherNumber: function() {
        const year = new Date().getFullYear().toString().slice(-2);
        const sequence = Math.floor(Math.random() * 90000) + 1; // 00001-99999
        return `T${year}${String(sequence).padStart(5, '0')}`;
    },

    /**
     * 生成用户名
     * 学生：使用学号作为用户名
     * 教师：使用工号作为用户名
     */
    generateUsername: function(number) {
        return number.toLowerCase();
    },

    /**
     * 生成初始密码
     * 默认：123456
     * 可选：基于编号生成
     */
    generatePassword: function(number = null) {
        // 默认密码
        const defaultPassword = '123456';
        
        // 如果需要基于编号生成密码
        if (number) {
            // 取编号后6位作为密码
            return number.slice(-6);
        }
        
        return defaultPassword;
    },

    /**
     * 生成随机密码
     * 包含大小写字母和数字，长度8-12位
     */
    generateRandomPassword: function(length = 8) {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let password = '';
        for (let i = 0; i < length; i++) {
            password += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return password;
    },

    /**
     * 创建用户账号数据
     */
    createUserData: function(options) {
        const { 
            username, 
            password, 
            realName, 
            phoneNumber = '', 
            email = '', 
            roleId 
        } = options;

        return {
            username: username,
            password: password,
            realName: realName,
            phoneNumber: phoneNumber,
            email: email,
            roleId: roleId,
            isActive: 1
        };
    },

    /**
     * 显示生成的账号信息
     */
    showAccountInfo: function(type, number, username, password) {
        const typeText = type === 'student' ? '学生' : '教师';
        const numberLabel = type === 'student' ? '学号' : '工号';
        
        return `
            <div style="background: #f0f9ff; padding: 15px; border-radius: 8px; border-left: 4px solid #3498db;">
                <h4 style="margin: 0 0 10px 0; color: #2c3e50;">✅ ${typeText}账号已创建</h4>
                <p style="margin: 5px 0;"><strong>${numberLabel}：</strong>${number}</p>
                <p style="margin: 5px 0;"><strong>用户名：</strong>${username}</p>
                <p style="margin: 5px 0;"><strong>初始密码：</strong><span style="color: #e74c3c; font-weight: bold;">${password}</span></p>
                <p style="margin: 10px 0 0 0; font-size: 0.9em; color: #7f8c8d;">
                    ⚠️ 请妥善保管账号信息，并提醒${typeText}首次登录后修改密码
                </p>
            </div>
        `;
    }
};

// 导出到全局
window.Generator = Generator;

