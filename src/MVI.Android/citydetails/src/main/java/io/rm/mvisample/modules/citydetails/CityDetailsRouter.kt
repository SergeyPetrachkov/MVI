package io.rm.mvisample.modules.citydetails

import android.app.Activity
import io.rm.mvi.router.Router
import io.rm.mvi.router.RouterInput

interface CityDetailsRouterInput : RouterInput<Activity>

class CityDetailsRouter : Router<Activity>(), CityDetailsRouterInput