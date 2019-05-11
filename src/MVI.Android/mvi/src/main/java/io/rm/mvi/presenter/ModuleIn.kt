package io.rm.mvi.presenter

import java.io.Serializable

open class ModuleIn : Serializable

open class CollectionModuleIn(val pageSize: Int) : ModuleIn()