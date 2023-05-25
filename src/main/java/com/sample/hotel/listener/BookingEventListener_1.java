package com.sample.hotel.listener;

import com.sample.hotel.entity.Booking;
import com.sample.hotel.entity.BookingStatus;
import io.jmix.core.DataManager;
import io.jmix.core.FluentLoader;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BookingEventListener_1 {

    @Autowired
    private DataManager dataManager;

    // Реализуйте автоматическое освобождение комнаты (Room), если назначенный на нее заказ (Booking) отменяется
    @EventListener
    public void onBookingChangedBeforeCommit(EntityChangedEvent<Booking> event) {
        if (event.getType() == EntityChangedEvent.Type.UPDATED &&
                event.getChanges().isChanged("status")) {

            Booking booking = dataManager.load(event.getEntityId()).one();
            if (booking.getRoomReservation() != null && booking.getStatus() == BookingStatus.CANCELLED) {
                dataManager.remove(booking.getRoomReservation());
            }

        }
    }


}