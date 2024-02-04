package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        ParkingLot parkingLot ;
        try{
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        User user;
        try{
             user = userRepository3.findById(userId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }
        List<Spot> spotList = parkingLot.getSpotList();
        List<Spot> spotRule = new ArrayList<>();
        for(Spot s : spotList){
            if(s.getOccupied() == false){
                int capacity;
                if(s.getSpotType() == SpotType.TWO_WHEELER){
                    capacity = 2;
                }
                else if(s.getSpotType() == SpotType.FOUR_WHEELER){
                    capacity = 4;
                }else{
                    capacity = Integer.MAX_VALUE;
                }
                if(capacity >= numberOfWheels){
                    spotRule.add(s);
                }
            }
        }

        //check spots are available r not
        if(spotRule.isEmpty() == true){
            throw new Exception("Cannot make reservation");
        }

        // check low price spot reservation
        Spot reserveSpot = null;
        int minPrice = Integer.MAX_VALUE;
        for(Spot spot : spotRule){
            int price = spot.getPricePerHour() * timeInHours;
            if(price < minPrice){
                minPrice = price;
                reserveSpot = spot;
            }
        }
        reserveSpot.setOccupied(true);

        Reservation reservation = new Reservation();
        reservation.setSpot(reserveSpot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);
        reservation.setPayment(null);

        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(reserveSpot);

        return reservation;
    }
}
