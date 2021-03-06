package com.bdkj.wirecrimping.bean;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bdkj.wirecrimping.Constant;
import com.bdkj.wirecrimping.util.JsonUtil;
import com.bdkj.wirecrimping.util.SPUtil;
import com.bdkj.wirecrimping.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MyObject {
    private Context mcontext;
    private int index = 0;
    private List<String> modelList = new ArrayList<>();//型号
    private List<String> wireList = new ArrayList<>();//使用导线
    private String sign;


    //构造函数传入上下文
    public MyObject(Context c, String sign) {
        mcontext = c;
        this.sign = sign;
        String STANDARDVALUES;
        if(sign.equals("1") || sign.equals("4")){
            STANDARDVALUES= SPUtil.getInstance(mcontext).getString(Constant.STANDARDVALUES);
        }else{
            STANDARDVALUES=SPUtil.getInstance(mcontext).getString(Constant.STANDARDVALUESTWO);
        }
        if (!TextUtils.isEmpty(STANDARDVALUES)) {
            List<StandardValuesBean.DataBean> dataBeanList = new ArrayList<>();
            dataBeanList.addAll(JsonUtil.stringToList(STANDARDVALUES, StandardValuesBean.DataBean.class));
            for (int i = 0; i < dataBeanList.size(); i++) {
                modelList.add(dataBeanList.get(i).getModel());
                wireList.add(dataBeanList.get(i).getApplyWire());
            }
        }
    }

    @JavascriptInterface
    public void showList() {
        //创建对话框
        if (modelList.size()>0) {
            String[] items = modelList.toArray(new String[modelList.size()]);
            AlertDialog alertDialog = new AlertDialog.Builder(mcontext)
                    .setTitle("型号列表")
                    .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            index = i;
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ModelBean modelBean = new ModelBean();
                            modelBean.setModel("型号");
                            modelBean.setResultModel(items[index]);
                            SPUtil.getInstance(mcontext).addString(Constant.MODEL, items[index]);
                            EventBus.getDefault().post(modelBean);

                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        } else {
            ToastUtils.showShort("暂无压接管型号数据");
        }
    }

    @JavascriptInterface
    public void showWireList() {
        //创建对话框
//        if (wireList.size()>0) {
//            String[] items = wireList.toArray(new String[wireList.size()]);
//            AlertDialog alertDialog = new AlertDialog.Builder(mcontext)
//                    .setTitle("导线列表")
//                    .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            index = i;
//                        }
//                    })
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            ModelBean modelBean = new ModelBean();
//                            modelBean.setModel("导线");
//                            modelBean.setResultModel(items[index]);
//                            SPUtil.getInstance(mcontext).addString(Constant.MAIN_DX_MODEL, items[index]);
//                            EventBus.getDefault().post(modelBean);
//
//                        }
//                    })
//                    .setNegativeButton("取消", null)
//                    .create();
//            alertDialog.show();
//        } else {
//            ToastUtils.showShort("暂无导线数据");
//        }
    }


    /**
     * gqwmax：表示点击的是钢管-压前值-外径-最大
     * gqwmin：表示点击的是钢管-压前值-外径-最小
     * @param message
     */
    @JavascriptInterface
    public void showValue(String message) {
        Log.e("tag",message+"+++++++++++++++");
        JSValueBean jsValueBean = new JSValueBean();
        JSONArray array = JSON.parseArray(message);
        for (int i = 0; i < array.size(); i++) {
            if ("1".equals(sign) || "2".equals(sign)) {
                if (i == 0) {
                    jsValueBean.setGqwmax(array.getString(0));//点击的是钢管还是铝管等
                    SPUtil.getInstance(mcontext).addString(Constant.MODEL_OR_CONDUCTOR, array.getString(0));
                } else {
                    jsValueBean.setGqwnum1(array.getString(1));
                }
            } else {
                if (i == 1) {
                    jsValueBean.setGqwnum1(array.getString(1));
                } else if (i == 2) {
                    jsValueBean.setGqwmax(array.getString(2));//点击的是钢管还是铝管等
                    SPUtil.getInstance(mcontext).addString(Constant.MODEL_OR_CONDUCTOR, array.getString(2));
                }

            }
        }
        jsValueBean.setMaxOrminDes("value");
        EventBus.getDefault().post(jsValueBean);
    }

    @JavascriptInterface
    public void showPhoto() {
        EventBus.getDefault().post("选择图片");
    }

    @JavascriptInterface
    public void amplificationPhoto(String photoStr) {
        PhotoAddressBean photoAddressBean = new PhotoAddressBean();
        photoAddressBean.setPhotoDes("放大图片");
        photoAddressBean.setPhotoSelect(photoStr);
        EventBus.getDefault().post(photoAddressBean);
    }

    @JavascriptInterface
    public void narrowPhoto() {
        EventBus.getDefault().post("缩小图片");
    }


    /**
     * 保存弯曲度测量的数据
     * @param message
     */
    @JavascriptInterface
    public void save(String message) {
        if ("1".equals(sign)) {
            SPUtil.getInstance(mcontext).addString(Constant.CURVATURESTRAIGHTSAVE, message);
        } else if ("2".equals(sign)) {
            SPUtil.getInstance(mcontext).addString(Constant.CURVATURETENSIONSAVE, message);
        } else if ("3".equals(sign)) {
            SPUtil.getInstance(mcontext).addString(Constant.CURVATURETHREESAVE, message);
        } else {
            SPUtil.getInstance(mcontext).addString(Constant.CURVATURETWOSAVE, message);
        }
        Log.e("弯曲度测量数据：","++++++++++++++"+message);
        AttributeValuesBean attributeValuesBean = (AttributeValuesBean) JsonUtil.stringToObject(message, AttributeValuesBean.class);
        attributeValuesBean.setMessage("保存");
        EventBus.getDefault().post(attributeValuesBean);
    }


    /**保存“金具复测及对边测量”的数据
     * @param message
     */
    @JavascriptInterface
    public void hardwareSave(String message) {
        if ("1".equals(sign)) {
            SPUtil.getInstance(mcontext).addString(Constant.HARDWARESTRAIGHTSAVE, message);
        } else if ("2".equals(sign)) {
            SPUtil.getInstance(mcontext).addString(Constant.HARDWARETENSIONSAVE, message);
        } else if ("3".equals(sign)) {
            SPUtil.getInstance(mcontext).addString(Constant.HARDWARETHREESAVE, message);
        } else {
            SPUtil.getInstance(mcontext).addString(Constant.HARDWARETWOSAVE, message);
        }
        Log.e("金具复测及对边测量：","++++++++++++++"+message);
        if ("1".equals(sign) || "2".equals(sign)) {
            HardwareBean hardwareBean = (HardwareBean) JsonUtil.stringToObject(message, HardwareBean.class);
            hardwareBean.setSaveInformation("保存");
            EventBus.getDefault().post(hardwareBean);
        } else {
            HardwareTwoBean hardwareTwoBean = (HardwareTwoBean) JsonUtil.stringToObject(message, HardwareTwoBean.class);
            hardwareTwoBean.setSaveInformation("保存数据");
            EventBus.getDefault().post(hardwareTwoBean);
        }

    }

    @JavascriptInterface
    public void getValues(String tex) {
        ByValueBean byValueBean = new ByValueBean();
        byValueBean.setTitle("传值");
        byValueBean.setValue(tex);
        EventBus.getDefault().post(byValueBean);
    }

}
