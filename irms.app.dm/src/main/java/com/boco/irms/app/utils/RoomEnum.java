package com.boco.irms.app.utils;

import com.boco.common.util.lang.GenericEnum;

public class RoomEnum {

    public static class Ownership extends GenericEnum{
        public static final int _ownership_0 = 0;
        public static final int _ownership_1 = 1;
        public static final int _ownership_2 = 2;
        public static final int _ownership_3 = 3;
        public static final int _ownership_4 = 4;
        public static final int _ownership_5 = 5;
        public static final int _ownership_6 = 6;
        Ownership() {
            super.putEnum(_ownership_0, "未知");
            super.putEnum(_ownership_1, "自建");
            super.putEnum(_ownership_2, "共建");
            super.putEnum(_ownership_3, "合建");
            super.putEnum(_ownership_4, "租用");
            super.putEnum(_ownership_5, "购买");
            super.putEnum(_ownership_6, "置换");
        }
    }


    public static class SeviceLevel extends GenericEnum{
        public static final int _seviceLeve_1 = 1;
        public static final int _seviceLeve_2 = 2;
        public static final int _seviceLeve_3 = 3;
        public static final int _seviceLeve_4 = 4;
        public static final int _seviceLeve_5 = 5;
        public static final int _seviceLeve_6 = 6;
        SeviceLevel() {
            super.putEnum(_seviceLeve_1, "省际");
            super.putEnum(_seviceLeve_2, "省内");
            super.putEnum(_seviceLeve_3, "省内/本地");
            super.putEnum(_seviceLeve_4, "本地骨干");
            super.putEnum(_seviceLeve_5, "本地汇聚");
            super.putEnum(_seviceLeve_6, "本地接入");
        }
    }
    


    public static class keyType extends GenericEnum{
        public static final int _keyType_1 = 1;
        public static final int _keyType_2 = 2;
        keyType() {
            super.putEnum(_keyType_1, "普通钥匙");
            super.putEnum(_keyType_2, "电子钥匙");
        }
    }

    public static class ReceiveRoomType extends GenericEnum{
        public static final int _keyType_1 = 1;
        public static final int _keyType_2 = 2;
        ReceiveRoomType() {
            super.putEnum(_keyType_1, "驻地网");
            super.putEnum(_keyType_2, "专线");
        }
    }

    public static class MaintMode extends GenericEnum{
        public static final int _maintMode_1 = 1;
        public static final int _maintMode_2 = 2;
        MaintMode() {
            super.putEnum(_maintMode_1, "自维");
            super.putEnum(_maintMode_2, "代维");
        }
    }

    public static class State extends GenericEnum{
        public static final int _state_1 = 1;
        public static final int _state_2 = 2;
        public static final int _state_3 = 3;
        public static final int _state_4 = 4;
        public static final int _state_5 = 5;
        State() {
            super.putEnum(_state_1, "设计");
            super.putEnum(_state_2, "在建");
            super.putEnum(_state_3, "竣工");
            super.putEnum(_state_4, "废弃");
            super.putEnum(_state_5, "维护");
        }
    }
    
    


    public static class MainDeviceType extends GenericEnum{
        public static final int _mainDeviceType_1 = 1;
        public static final int _mainDeviceType_2 = 2;
        public static final int _mainDeviceType_3 = 3;
        public static final int _mainDeviceType_4 = 4;
        public static final int _mainDeviceType_5 = 5;
        public static final int _mainDeviceType_6 = 6;
        public static final int _mainDeviceType_7 = 7;
        public static final int _mainDeviceType_8= 8;
        public static final int _mainDeviceType_9 = 9;
        public static final int _mainDeviceType_10 = 10;
        MainDeviceType() {
            super.putEnum(_mainDeviceType_1, "SDH");
            super.putEnum(_mainDeviceType_2, "PDH");
            super.putEnum(_mainDeviceType_3, "WDM");
            super.putEnum(_mainDeviceType_4, "微波");
            super.putEnum(_mainDeviceType_5, "混合");
            super.putEnum(_mainDeviceType_6, "HDSL");
            super.putEnum(_mainDeviceType_7, "光猫");
            super.putEnum(_mainDeviceType_8, "协转");
            super.putEnum(_mainDeviceType_9, "2M直连");
            super.putEnum(_mainDeviceType_10, "未知");
        }
    }
    

    public static class SpecialLineLevel extends GenericEnum{
        public static final int _specialLineLevel_1 = 1;
        public static final int _specialLineLevel_2 = 2;
        public static final int _specialLineLevel_3 = 3;
        SpecialLineLevel() {
            super.putEnum(_specialLineLevel_1, "金牌");
            super.putEnum(_specialLineLevel_2, "银牌");
            super.putEnum(_specialLineLevel_3, "普通");
        }
    }
    
    public static class RoomType extends GenericEnum{
        public static final int _roomType1 = 1;
        public static final int _roomType2 = 2;
        public static final int _roomType3 = 3;
        public static final int _roomType4 = 4;
        public static final int _roomType5 = 5;
        public static final int _roomType6 = 6;
        public static final int _roomType7 = 7;
        public static final int _roomType8= 8;
        public static final int _roomType9 = 9;
        public static final int _roomType10 = 10;
        RoomType() {
            super.putEnum(_roomType1, "传输");
            super.putEnum(_roomType2, "交换");
            super.putEnum(_roomType3, "传输");
            super.putEnum(_roomType4, "客服");
            super.putEnum(_roomType5, "网管");
            super.putEnum(_roomType6, "计费");
            super.putEnum(_roomType7, "IT中心");
            super.putEnum(_roomType8, "其它");
            super.putEnum(_roomType9, "基站");
            super.putEnum(_roomType10, "综合机房");
        }
    }
}
