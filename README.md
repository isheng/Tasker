### Tasker —— Android智能助手
### 一、已实现功能
- 飞行模式切换
- 检测当前的APP
- 电量监控
- 蓝牙开关
- 亮度调节
- 壁纸
- 手机朝上/下检测
- 来电事件
- 设置铃声
- 短信事件
- WIFI开关

### 二、重要概念
1. Scene  
    Scene表示一个场景，具体包括了一些预定义的Condition和Action，当所有的条件都满足的情况下，由用户预设的Action将会自动执行。
2. Condition  
    Condition表示执行预设的Action需要满足的条件。目前支持的Condition有：  
    - 当前运行的APP
    - 电量低于、高于某个值
    - 手机朝上/朝下
    - 特定号码来电
    - 收到特定号码、内容的短信

3. Action  
    Action表示满足Condition以后执行的预设动作。目前支持的Action有：
    - 飞行模式开关
    - 蓝牙开关
    - 亮度调节
    - 设置壁纸
    - 设置铃声
    - 设置响铃模式
    - WiFi开关


