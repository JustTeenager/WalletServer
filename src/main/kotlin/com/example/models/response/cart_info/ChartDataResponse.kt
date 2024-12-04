package com.example.models.response.cart_info

import com.example.models.response.cart_info.CategoriesByMonthResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность аналитики для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class ChartDataResponse(
    @SerialName("income_by_month")
    val incomeByMonth: String,
    @SerialName("income_percent")
    val incomePercent: Float,
    @SerialName("outcome_by_month")
    val outcomeByMonth: String,
    @SerialName("outcome_percent")
    val outcomePercent: Float,
    @SerialName("categories")
    val categories: List<CategoriesByMonthResponse>,
    @SerialName("date")
    val date: Long,
)