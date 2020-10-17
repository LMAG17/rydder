package com.chefmenu.nami.models.detailModels

data class DetailResponse(
    val order: Order,
    val message: String? = null
)

data class Order(
    val id: Int,
    val comments: String,
    val email: String,
    val turns: String,
    val deliveryValue: String,
    val service: String,
    val typeDocument: String? = null,
    val identification: String? = null,
    val reasonNotDelivery: ReasonNotDelivery? = null,
    val detailOrder: DetailOrder
)

data class DetailOrder(
    val list: List<ListElement>
)

data class ListElement(
    val id: Int,
    val description: String? = null,
    val quantityArticle: String,
    val valueTotalArticle: String,
    val codOptionalsExternals: Int? = null,
    val codTamano: Int? = null,
    val observations: String? = null,
    val picking: String,
    val article: Article
)

data class Article(
    val id: Int,
    val name: String,
    val description: String,
    val value: String,
    val image: String,
    val upc: String,
    val sku: String
)

/*
data class DetailResponse (
    val id: Int,
    val comments: String,
    val email: String,
    val typeDocument:String?=null,
    val identification:String?=null,
    val turns: String,
    val deliveryValue: String,
    val service: String,
    val reasonNotDelivery: ReasonNotDelivery,
    val categories: List<Category>,
    val message:String?=null
)

data class Category (
    val name: String,
    val order: Int,
    val articles: List<Article>
)

data class Article (
    val id: Int,
    val description: String? = null,
    val quantityArticle: String,
    val valueTotalArticle: String,
    val observations: String? = null,
    val picking: Any? = null,
    val info: Info
)

data class Info (
    val id: Int,
    val name: String,
    val value: String,
    val image: String,
    val upc: String? = null,
    val sku: String,
    val idSubCategory: Int,
    val order: Int
)
*/

data class ReasonNotDelivery(
    val id: Int,
    val description: String
)
