package com.example.land_parcel.PDFReport.ReportModel.Response

data class ApiResponse(
    val ApiVersion: String,
    val Success: Boolean,
    val Data: DataObject?,
    val Errors: List<String>?
)

data class DataObject(
    val JsonResultSet: List<LandParcel>?,
    val TotalRows: Int
)

data class LandParcel(
    val KhasraNumber: Int,
    val VillageId: String,
    val VillageName: String?,
    val ReportURL: String?,
    val DistrictName: String?,
    val Tehsil: String?,
    val Owner: String?,
    val GovtId: String?,
    val FatherName: String?,
    val LandType: String?,
    val Area: Double?,
    val Status: Int,
    val CreatedDate: String?,
    val UpdatedDate: String?,
    val CreatedBy: String?,
    val UpdatedBy: String?,
    val MobileNo: String?,
    val PnilNo: String?,
    val HouseNo: Int?,
    val Block: String?,
    val ReportGeneratedOn: String,
    val LandParcelUpdatedOn: String
)

