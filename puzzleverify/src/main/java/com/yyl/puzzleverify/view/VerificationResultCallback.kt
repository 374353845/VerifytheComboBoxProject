package com.yyl.puzzleverify.view

interface VerificationResultCallback {

    /**
     * 验证成功
     */
    fun  succeed()

    /**
     * 验证失败
     */
    fun  failure()
}