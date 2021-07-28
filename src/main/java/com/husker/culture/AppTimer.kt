package com.husker.culture

import java.util.*
import java.util.function.Consumer

class AppTimer {
    companion object{

        fun create(period: Long, action: Consumer<Timer>): Timer{
            return create(0, period, action)
        }

        fun create(delay: Long, period: Long, action: Consumer<Timer>): Timer{
            val instance = Timer()
            instance.schedule(object: TimerTask(){
                override fun run() {
                    action.accept(instance)
                }
            }, delay, period)

            App.shutdownListeners.add{
                instance.cancel()
            }
            return instance
        }
    }
}