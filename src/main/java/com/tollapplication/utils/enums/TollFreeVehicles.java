package com.tollapplication.utils.enums;

public enum TollFreeVehicles {
        MOTORBIKE("m", "Motorbike"),
        TRACTOR("t", "Tractor"),
        EMERGENCY("e", "Emergency"),
        DIPLOMAT("d", "Diplomat"),
        FOREIGN("f", "Foreign"),
        MILITARY("mil", "Military");
        private final String code;
        private final String desc;

        TollFreeVehicles(String code, String type) {
                this.code = code;
                this.desc = type;
        }

        public String getCode() {
                return code;
        }

        public String getDesc() {
            return desc;
        }
}
