package com.sample.hotel.app;

import com.sample.hotel.entity.Booking;
import com.sample.hotel.entity.Room;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;

@Component

public class BookingService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private ObjectProvider<Notifications> notificationsProvider;


    /**
     * Check if given room is suitable for the booking.
     * 1) Check that sleeping places is enough to fit numberOfGuests.
     * 2) Check that there are no reservations for this room at the same range of dates.
     * Use javax.persistence.EntityManager and JPQL query for querying database.
     *
     * @param booking booking
     * @param room    room
     * @return true if checks are passed successfully
     */
    @Transactional
    public boolean isSuitable(Booking booking, Room room) {

        // Для выбранной комнаты в БД не должно быть уже назначенных бронирований (RoomReservation),
        // пересекающихся по сроку пребывания (с arrivalDate по departureDate) с текущим заказом.
        // Считаем, что клиент может въезжать в комнату в тот же день, когда предыдущий клиент выехал
        Query query = entityManager.createQuery(
                "SELECT e FROM RoomReservation e WHERE " +
                        "e.room = :room AND ( " +
                        "( :arrivalDate >= e.booking.arrivalDate AND :arrivalDate < e.booking.departureDate ) OR " +
                        "( :departureDate >= e.booking.arrivalDate AND :departureDate < e.booking.departureDate )  )"
        );
        query.setParameter("arrivalDate", booking.getArrivalDate());
        query.setParameter("departureDate", booking.getDepartureDate());
        query.setParameter("room", room);
        int resultsCount = query.getResultList().size();

        // Количество спальных мест в комнате (Room#sleepingPlaces) не должно быть меньше числа гостей (Booking#numberOfGuests).
        return (room.getSleepingPlaces() >= booking.getNumberOfGuests()) && (resultsCount == 0);

    }

    /**
     * Check that room is suitable for the booking, and create a reservation for this room.
     *
     * @param room    room to reserve
     * @param booking hotel booking
     *                Wrap operation into a transaction (declarative or manual).
     * @return created reservation object, or null if room is not suitable
     */
    @Transactional
    public RoomReservation reserveRoom(Booking booking, Room room) {
        RoomReservation roomReservation = null;
        if (isSuitable(booking, room)) {
            roomReservation = dataManager.create(RoomReservation.class);
            roomReservation.setBooking(booking);
            roomReservation.setRoom(room);
            dataManager.save(roomReservation);
        }
        return roomReservation;
    }
}