package gd.mmanage.model

/**
 * Created by Administrator on 2017/11/10.
 */
class NormalRequest<T> {
    constructor(code: Int, result: Boolean, obj: T?) {
        this.code = code
        this.result = result
        this.obj = obj
    }

    constructor(code: Int, obj: T?) {
        this.code = code
        this.obj = obj
    }


    var code: Int = 0//请求码0:请求成功。1：失败。2：报错
    var result: Boolean = false//是否解析成功
    var obj: T? = null
}