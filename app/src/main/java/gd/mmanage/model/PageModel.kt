package gd.mmanage.model

/**
 * Created by Administrator on 2017/11/20.
 */
class PageModel<T> {
    var PageIndex = 1//页码
    var PageSize = 20//每页条数
    var ItemCount = 0//总条数
    var PageCount = 0//总页数
    var data: List<T>? = null
}