package com.example.studentattendanceterminal.ui;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentattendanceterminal.R;
import com.example.studentattendanceterminal.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogViewerActivity extends AppCompatActivity {

    private TextView tvLogContent;
    private TextView tvLogPath;
    private static final int MAX_LOG_LINES = 1000; // 最多显示1000行，避免内存溢出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("日志查看器");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvLogContent = findViewById(R.id.tv_log_content);
        tvLogPath = findViewById(R.id.tv_log_path);

        // 设置TextView可滚动
        tvLogContent.setMovementMethod(new ScrollingMovementMethod());

        // 显示日志文件路径
        String logPath = LogUtil.getLogFilePath();
        tvLogPath.setText("日志路径: " + logPath);

        // 加载并显示日志内容
        loadLogContent();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadLogContent() {
        String logPath = LogUtil.getLogFilePath();
        if (logPath == null || logPath.equals("日志文件未初始化")) {
            tvLogContent.setText("日志文件未初始化，请先使用应用功能生成日志。");
            return;
        }

        File logFile = new File(logPath);
        if (!logFile.exists()) {
            tvLogContent.setText("日志文件不存在。\n\n路径: " + logPath);
            return;
        }

        try {
            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            int lineCount = 0;
            int totalLines = 0;

            // 先统计总行数
            while (reader.readLine() != null) {
                totalLines++;
            }
            reader.close();

            // 如果行数超过限制，只读取最后N行
            reader = new BufferedReader(new FileReader(logFile));
            if (totalLines > MAX_LOG_LINES) {
                // 跳过前面的行
                int skipLines = totalLines - MAX_LOG_LINES;
                for (int i = 0; i < skipLines; i++) {
                    reader.readLine();
                }
                content.append("(日志过长，仅显示最后 ").append(MAX_LOG_LINES).append(" 行)\n");
                content.append("(总行数: ").append(totalLines).append(")\n\n");
            }

            // 读取剩余的行
            while ((line = reader.readLine()) != null && lineCount < MAX_LOG_LINES) {
                content.append(line).append("\n");
                lineCount++;
            }
            reader.close();

            if (content.length() == 0) {
                tvLogContent.setText("日志文件为空。");
            } else {
                tvLogContent.setText(content.toString());
                // 滚动到底部
                tvLogContent.post(() -> {
                    if (tvLogContent.getLayout() != null) {
                        int scrollAmount = tvLogContent.getLayout().getLineTop(tvLogContent.getLineCount()) - tvLogContent.getHeight();
                        if (scrollAmount > 0) {
                            tvLogContent.scrollTo(0, scrollAmount);
                        } else {
                            tvLogContent.scrollTo(0, 0);
                        }
                    }
                });
            }

        } catch (IOException e) {
            tvLogContent.setText("读取日志文件失败: " + e.getMessage());
            Toast.makeText(this, "读取日志失败", Toast.LENGTH_SHORT).show();
        }
    }
}

