package md.edit.services.document.configuration.health

import org.springframework.boot.actuate.availability.LivenessStateHealthIndicator
import org.springframework.boot.availability.ApplicationAvailability
import org.springframework.boot.availability.AvailabilityState
import org.springframework.boot.availability.LivenessState
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class NotificationListenerIndicator(availability: ApplicationAvailability) : LivenessStateHealthIndicator(availability) {

    private val alive = AtomicBoolean()


    override fun getState(applicationAvailability: ApplicationAvailability): AvailabilityState {
        return if (alive.get())
            LivenessState.CORRECT
        else
            LivenessState.BROKEN
    }

    fun setAlive(alive: Boolean) {
        this.alive.set(alive)
    }
}