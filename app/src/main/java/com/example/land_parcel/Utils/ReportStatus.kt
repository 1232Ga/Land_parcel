package com.example.land_parcel.Utils

enum class ReportStatus(val code: Int) {
    NOTSTARTED(0),
    INITIATED(1),
    COMPLETED(2),
    UPDATED(3),
    FAILED(4);
    companion object {
        fun fromCode(code: Int): ReportStatus? {
            return values().find { it.code == code }
        }
    }
}
