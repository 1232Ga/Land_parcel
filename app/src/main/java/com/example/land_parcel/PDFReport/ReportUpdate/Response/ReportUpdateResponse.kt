package com.example.land_parcel.PDFReport.ReportUpdate.Response


import androidx.annotation.Keep

@Keep
data class ReportUpdateResponse(
    var ApiVersion: String,
    var Errors: List<Any>,
    var Message: String,
    var Success: Boolean
)