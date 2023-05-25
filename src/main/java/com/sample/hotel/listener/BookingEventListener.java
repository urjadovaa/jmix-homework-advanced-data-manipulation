package com.sample.hotel.listener;

import com.sample.hotel.entity.Booking;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingEventListener {

    // •	Реализуйте автоматическое вычисление и сохранение атрибута departureDate на основе arrivalDate и nightsOfStay.
    // •	Используйте для этого слушатель события EntitySavingEvent.
    // •	Атрибут должен обновляться как при создании, так и при изменении Booking.

    @EventListener
    public void onBookingSaving(EntitySavingEvent<Booking> event) {
        Booking entity = event.getEntity();
        LocalDate arrivalDate = entity.getArrivalDate();
        Integer nightsOfStay = entity.getNightsOfStay();
        entity.setDepartureDate(arrivalDate.plusDays(nightsOfStay));
    }


}