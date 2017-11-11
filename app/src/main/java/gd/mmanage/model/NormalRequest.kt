package gd.mmanage.model

/**
 * Created by Administrator on 2017/11/10.
 */
class NormalRequest<T> {
    constructor(code: Int, result: Boolean, obj: T) {
        this.code = code
        this.result = result
        this.obj = obj
    }

    constructor(result: Boolean, obj: T) {
        this.result = result
        this.obj = obj
    }


    var code: Int = 0//请求码
    var result: Boolean = false//请求的结果
    var obj: T? = null
}