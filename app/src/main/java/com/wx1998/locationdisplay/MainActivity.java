package com.wx1998.locationdisplay;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public final class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, AdapterView.OnItemSelectedListener {
    private AMap aMap;
    private MapView mapView;
    private Spinner spinnerGps;
    private String[] itemLocationTypes = {"展示", "定位", "追随", "旋转", "旋转位置", "追随不移动到中心点", "旋转不移动到中心点", "旋转位置不移动到中心点"};
    private static MainActivity application;

    private MyLocationStyle myLocationStyle;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //经纬度
    double latitude = 0, longitude = 0;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    latitude = amapLocation.getLatitude();//获取纬度
                    longitude = amapLocation.getLongitude();//获取经度
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    public static MainActivity getInstance() {
        return application;
    }

    //初始化AMapLocationClientOption对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        application = this;
        if (Build.VERSION.SDK_INT > 28 & getApplicationContext().getApplicationInfo().targetSdkVersion > 28) {
            needPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    BACK_LOCATION_PERMISSION
            };
            needCheckBackLocation = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();//初始化定位
    }

    /**
     * 初始化
     */
    private void init() {

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        spinnerGps = (Spinner) findViewById(R.id.spinner_gps);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemLocationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGps.setAdapter(adapter);
        spinnerGps.setOnItemSelectedListener(this);
        //设置SDK 自带定位消息监听
        aMap.setOnMyLocationChangeListener(this);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        showMe();
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 如果要设置定位的默认状态，可以在此处进行设置
        myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // 只定位，不进行其他操作
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
                break;
            case 1:
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
                break;
            case 2:
                // 设置定位的类型为 跟随模式
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));
                break;
            case 3:
                // 设置定位的类型为根据地图面向方向旋转
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE));
                break;
            case 4:
                // 定位、且将视角移动到地图中心点，定位点依照设备方向旋转， 并且会跟随设备移动。
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
                break;
            case 5:
                // 定位、但不会移动到地图中心点，并且会跟随设备移动。
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
                break;
            case 6:
                // 定位、但不会移动到地图中心点，地图依照设备方向旋转，并且会跟随设备移动。
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER));
                break;
            case 7:
                // 定位、但不会移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER));
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //我当前位置
            case R.id.my_current_location:
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));
                changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude, longitude), 18, 30, 30)));
                aMap.clear();
                return true;
            //用户实时位置
            case R.id.user_real_time_location:
                return true;
            //用户最后一次位置
            case R.id.uer_last_location:
                showCameraLocation(39.983456, 116.3154950);
                return true;
            //用户历史位置
            case R.id.user_historical_location:

                HashMap<Integer, ArrayList<Double>> data = new HashMap<>();
                ArrayList<Double> arrayList1;
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37052922);
                arrayList1.add(109.20578895);
                data.put(0, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37058922);
                arrayList1.add(109.20528895);
                data.put(1, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37066931);
                arrayList1.add(109.20533299);
                data.put(2, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37056641);
                arrayList1.add(109.20539679);
                data.put(3, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37054409);
                arrayList1.add(109.20542163);
                data.put(4, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37041297);
                arrayList1.add(109.20544519);
                data.put(5, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37044223);
                arrayList1.add(109.20544042);
                data.put(6, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37043041);
                arrayList1.add(109.20544218);
                data.put(7, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37041903);
                arrayList1.add(109.20544947);
                data.put(8, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37041915);
                arrayList1.add(109.2054027);
                data.put(9, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37043298);
                arrayList1.add(109.20530932);
                data.put(10, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37042337);
                arrayList1.add(109.20628903);
                data.put(11, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.3704149);
                arrayList1.add(109.2052828);
                data.put(12, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.37040197);
                arrayList1.add(109.20528235);
                data.put(13, arrayList1);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.38099197);
                arrayList1.add(109.20998235);
                data.put(14, arrayList1);
                drawLineInMap(data);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.38099197);
                arrayList1.add(109.20698235);
                data.put(15, arrayList1);
                drawLineInMap(data);
                arrayList1 = new ArrayList<>();
                arrayList1.add(34.38099197);
                arrayList1.add(109.20698235);
                data.put(16, arrayList1);
                drawLineInMap(data);
                return true;
            //选择用户
            case R.id.select_user:
                startActivity(new Intent(MainActivity.this, UserList.class));
                return true;
            //设置
            case R.id.set:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /*********************************************************************************************/


    /**
     * 显示我所在位置
     */
    private void showMe() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                showCameraLocation(latitude, longitude);
                aMap.clear();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 2000);//1秒后执行TimeTask的run方法
    }

    private void showCameraLocation(double latitude, double longitude) {
        showCameraLocation(new LatLng(latitude, longitude));
    }

    private void showCameraLocation(LatLng latLng) {
        //切到当前位置
        aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 30, 30)));
    }


    /**
     * 画线
     */
    private void drawLineInMap(HashMap<Integer, ArrayList<Double>> data) {
        double ylatitude = 0;
        double ylongitude = 0;
        List<LatLng> latLngs = new ArrayList<LatLng>();
        Object[] key_arr = data.keySet().toArray();
        Arrays.sort(key_arr);
        for (Object key : key_arr) {
            ArrayList<Double> value = (ArrayList<Double>) data.get(key);
            ylatitude = value.get(0);
            ylongitude = value.get(1);
            latLngs.add(new LatLng(ylatitude, ylongitude));

        }
        aMap.clear();
        aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
        //showCameraLocation(ylatitude, ylongitude);

        addMarkersToMap(new LatLng(data.get(0).get(0), data.get(0).get(1)),"开始","开始详细信息");
        addMarkersToMap(new LatLng(data.get(data.size() - 2).get(0), data.get(data.size() - 2).get(1)),"结束","结束详细信息");

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(data.get(0).get(0), data.get(0).get(1)));
        builder.include(new LatLng(data.get(data.size() - 2).get(0), data.get(data.size() - 2).get(1)));

        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng,String title ,String snippet) {
        aMap.addMarker(
                new MarkerOptions().title(title)
                        .snippet(snippet).icon(BitmapDescriptorFactory .fromView(getBitmapView(this.getApplicationContext(),title,snippet)))
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        ).showInfoWindow();
    }
    public static View getBitmapView(Context context,String title ,String snippet)
    {
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.mark_info, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_snippet = (TextView) view.findViewById(R.id.tv_snippet);
        tv_title.setText(title);
        tv_snippet.setText(snippet);
        return view;
    }
    /*********************************************************************************************/


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if (location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);

                /*
                errorCode
                errorInfo
                locationType
                */
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }

        } else {
            Log.e("amap", "定位失败");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }


    /*************************************** 权限检查******************************************************/

    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            BACK_LOCATION_PERMISSION
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
//        UiSettings uiSettings =  aMap.getUiSettings();
//        uiSettings.setScrollGesturesEnabled(true);
        try {
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if (!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        aMap.moveCamera(update);

    }
}
