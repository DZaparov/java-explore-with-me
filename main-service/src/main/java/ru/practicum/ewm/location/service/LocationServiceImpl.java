package ru.practicum.ewm.location.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.location.LocationRepository;
import ru.practicum.ewm.location.model.Location;

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }
}
