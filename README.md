# v1.7.5
## 更新说明
增加登录获取token和上传

## API

### Login
获取上传需要的 token
```
UploadManager uploadManager = new UploadManager(MainActivity.this);
uploadManager.setLoginListener(new LoginListener() {
    @Override
    public void OnError(String errorInfo) {
        Log.d(TAG, "--- login error = " + errorInfo);
    }

    @Override
    public void OnSuccess(String token) {

        Log.d(TAG, "--- token = " + token);
        MainActivity.this.token = token;
    }
});
uploadManager.login(username, password);
```


### upload

```
UploadManager uploadManager = new UploadManager(MainActivity.this);
uploadManager.setUploadListener(new UploadListener() {
    @Override
    public void OnError(String errorInfo) {

	Log.d(TAG, "--- errorInfo = " + errorInfo);
    }

    @Override
    public void OnProgress(int index, int progress) {
	Log.d(TAG, "--- int = " + index + " progress = " + progress);
    }

    @Override
    public void OnSuccess(String info) {

	Log.d(TAG, "--- OnSuccess = " + info);
    }
});

ProductionInfo info = new ProductionInfo();

info.setProvince("广东省");
info.setCity("深圳市");
info.setArea("南山区");
//商圈
info.setDistrict("广东省");
//小区
info.setVillage("钓鱼台");
//房屋面积
info.setMianji("55");
//楼号
info.setHouseNumber("6");
//房号
info.setRoomNumber("4");
//几室几厅几卫
info.setRoom("1");
info.setHall("2");
info.setToilet("3");
info.setPrice("2500000");
info.setIntroduce("房源描述");
//设备wifi名称
info.setMacAddress("IPANO2T-e0b94df478fd");
//照片拍摄的时间戳
String timestamp = "20171220091255";
info.setGroup_id(timestamp);
//场景数量
int group_count = 2;
info.setGroup_count(group_count);

List<SingleInfo> uploadList = new ArrayList<SingleInfo>();
for (int i = 0; i < group_count; i++) {
    SingleInfo singleInfo = new SingleInfo();
    // 场景图片的路径
    singleInfo.setImageDir(Environment.getExternalStorageDirectory() + "/pano/20171220091252/Masterroom");
    // location 该场景名称
    singleInfo.setLocation("主卧");
    singleInfo.setGroup_id(timestamp);
    // 场景 pano2.jpg 的地址
    singleInfo.setOnetotwo_imageUrl(Environment.getExternalStorageDirectory() + "/pano/20171220091252/MasterroomIpanoTwo/pano2.jpg");
    // 设备标识 固定
    singleInfo.setDevice("2t");
    uploadList.add(singleInfo);
}
uploadManager.upload(token, info, uploadList);
```



全景链接
`http://wx.sz-sti.com/scene?key=<key>`

## demo
- 复制 pano到sd卡根目录
- 点击login获取token
- 点击上传获取key
- 打开全景链接`http://wx.sz-sti.com/scene?key=`+key
