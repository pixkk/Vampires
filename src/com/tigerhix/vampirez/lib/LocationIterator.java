package com.tigerhix.vampirez.lib;

import org.bukkit.block.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import java.util.*;
//import java.util.Vector;
import org.bukkit.util.Vector;

public class LocationIterator implements Iterator<Location>
{
//    private static final int gridSize = 16777216;
    private boolean end;
    private Location[] locationQueue;
    private int currentLocation;
    private int currentDistance;
    private int maxDistanceInt;
    private int secondError;
    private int thirdError;
    private int secondStep;
    private int thirdStep;
    private BlockFace mainFace;
    private BlockFace secondFace;
    private BlockFace thirdFace;
    
    public LocationIterator(final World world, final Vector vector, final Vector vector2, final double yOffset, final int maxDistance) {
        this.end = false;
        this.locationQueue = new Location[3];
        this.currentLocation = 0;
        this.currentDistance = 0;
        final Object startClone = vector.clone();
        ((Vector) startClone).setY(((Vector) startClone).getY() + yOffset);
        this.currentDistance = 0;
        final Location startLocation = new Location(world, (double)(int)Math.floor(((Location) startClone).getX()), (double)(int)Math.floor(((Location) startClone).getY()), (double)(int)Math.floor(((Location) startClone).getZ()));
        this.mainFace = this.getXFace(vector2);
        double mainDirection = this.getXLength(vector2);
        double mainPosition = this.getXPosition(vector2, startClone, startLocation);
        this.secondFace = this.getYFace(vector2);
        double secondDirection = this.getYLength(vector2);
        double secondPosition = this.getYPosition(vector2, (Vector) startClone, startLocation);
        this.thirdFace = this.getZFace(vector2);
        double thirdDirection = this.getZLength(vector2);
        double thirdPosition = this.getZPosition(vector2, (Vector) startClone, startLocation);
        if (this.getYLength(vector2) > mainDirection) {
            this.mainFace = this.getYFace(vector2);
            mainDirection = this.getYLength(vector2);
            mainPosition = this.getYPosition(vector2, (Vector) startClone, startLocation);
            this.secondFace = this.getZFace(vector2);
            secondDirection = this.getZLength(vector2);
            secondPosition = this.getZPosition(vector2, (Vector) startClone, startLocation);
            this.thirdFace = this.getXFace(vector2);
            thirdDirection = this.getXLength(vector2);
            thirdPosition = this.getXPosition(vector2, startClone, startLocation);
        }
        if (this.getZLength(vector2) > mainDirection) {
            this.mainFace = this.getZFace(vector2);
            mainDirection = this.getZLength(vector2);
            mainPosition = this.getZPosition(vector2, (Vector) startClone, startLocation);
            this.secondFace = this.getXFace(vector2);
            secondDirection = this.getXLength(vector2);
            secondPosition = this.getXPosition(vector2, startClone, startLocation);
            this.thirdFace = this.getYFace(vector2);
            thirdDirection = this.getYLength(vector2);
            thirdPosition = this.getYPosition(vector2, (Vector) startClone, startLocation);
        }
        final double d = mainPosition / mainDirection;
        final double secondd = secondPosition - secondDirection * d;
        final double thirdd = thirdPosition - thirdDirection * d;
        this.secondError = (int)Math.floor(secondd * 1.6777216E7);
        this.secondStep = (int)Math.round(secondDirection / mainDirection * 1.6777216E7);
        this.thirdError = (int)Math.floor(thirdd * 1.6777216E7);
        this.thirdStep = (int)Math.round(thirdDirection / mainDirection * 1.6777216E7);
        if (this.secondError + this.secondStep <= 0) {
            this.secondError = -this.secondStep + 1;
        }
        if (this.thirdError + this.thirdStep <= 0) {
            this.thirdError = -this.thirdStep + 1;
        }
        Location lastLocation = this.getRelativeLocation(startLocation, this.reverseFace(this.mainFace));
        if (this.secondError < 0) {
            this.secondError += 16777216;
            lastLocation = this.getRelativeLocation(lastLocation, this.reverseFace(this.secondFace));
        }
        if (this.thirdError < 0) {
            this.thirdError += 16777216;
            lastLocation = this.getRelativeLocation(lastLocation, this.reverseFace(this.thirdFace));
        }
        this.secondError -= 16777216;
        this.thirdError -= 16777216;
        this.locationQueue[0] = lastLocation;
        this.currentLocation = -1;
        this.scan();
        boolean startLocationFound = false;
        for (int cnt = this.currentLocation; cnt >= 0; --cnt) {
            if (this.locationEquals(this.locationQueue[cnt], startLocation)) {
                this.currentLocation = cnt;
                startLocationFound = true;
                break;
            }
        }
        if (!startLocationFound) {
            throw new IllegalStateException("Start location missed in LocationIterator");
        }
        this.maxDistanceInt = (int)Math.round(maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));
    }
    
    private boolean locationEquals(final Location a, final Location b) {
        return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
    }
    
    private BlockFace reverseFace(final BlockFace face) {
        switch (face) {
            case UP: {
                return BlockFace.DOWN;
            }
            case DOWN: {
                return BlockFace.UP;
            }
            case NORTH: {
                return BlockFace.SOUTH;
            }
            case SOUTH: {
                return BlockFace.NORTH;
            }
            case EAST: {
                return BlockFace.WEST;
            }
            case WEST: {
                return BlockFace.EAST;
            }
            default: {
                return null;
            }
        }
    }
    
    private BlockFace getXFace(Vector direction) {
    	if (direction.getX() > 0.0) {
    		return BlockFace.SOUTH;
    	}
    	else {
    		return BlockFace.NORTH;
    	}
        
    	//return (BlockFace) direction.get(secondError);
    }
    
    private BlockFace getYFace(final Vector direction) {
        return (direction.getY() > 0.0) ? BlockFace.UP : BlockFace.DOWN;
    	//return (BlockFace) direction.get(currentLocation);
    }
    
    private BlockFace getZFace(final Vector direction) {
        return (direction.getZ() > 0.0) ? BlockFace.WEST : BlockFace.EAST;
    	//return (BlockFace) direction.get(currentLocation);
    }
    
    private double getXLength(final Vector direction) {
        return Math.abs((direction).getX());
    	//return (double) direction.get(currentLocation);
    }
    
    private double getYLength(final Vector direction) {
        return Math.abs(direction.getY());
    	//return (double) direction.get(currentLocation);
    }
    
    private double getZLength(final Vector direction) {
        return Math.abs(direction.getZ());
    	//return (double) direction.get(currentLocation);
    }
    
    private double getPosition(final double direction, final double position, final int locationPosition) {
        return (direction > 0.0) ? (position - locationPosition) : (locationPosition + 1 - position);
    }
    
    private double getXPosition(final Vector direction, final Object startClone, final Location location) {
        return this.getPosition((direction).getX(), ((Vector) startClone).getX(), location.getBlockX());
    	//return this.getPosition(gridSize, currentLocation, currentDistance);
    	//return this;
    }
    
    private double getYPosition(final Vector direction,final Object startClone, final Location location) {
    	return this.getPosition((direction).getY(), ((Vector) startClone).getY(), location.getBlockY());
    	//return this.getPosition(gridSize, currentLocation, currentDistance);
    }
    
    private double getZPosition(final Vector direction, final Object startClone, final Location location) {
    	return this.getPosition((direction).getY(), ((Vector) startClone).getY(), location.getBlockY());
    	//return this.getPosition(gridSize, currentLocation, currentDistance);
    }
    
    public LocationIterator(final Location loc, final double yOffset, final int maxDistance) {
        this(loc.getWorld(), loc.toVector(), loc.getDirection(), yOffset, maxDistance);
    }
    
    public LocationIterator(final Location loc, final double yOffset) {
        this(loc.getWorld(), loc.toVector(), loc.getDirection(), yOffset, 0);
    }
    
    public LocationIterator(final Location loc) {
        this(loc, 0.0);
    }
    
    public LocationIterator(final LivingEntity entity, final int maxDistance) {
        this(entity.getLocation(), entity.getEyeHeight(), maxDistance);
    }
    
    public LocationIterator(final LivingEntity entity) {
        this(entity, 0);
    }
    
    public Location getRelativeLocation(final Location location, final BlockFace face) {
        switch (face) {
            case UP: {
                return location.clone().add(0.0, 1.0, 0.0);
            }
            case DOWN: {
                return location.clone().add(0.0, -1.0, 0.0);
            }
            case NORTH: {
                return location.clone().add(-1.0, 0.0, 0.0);
            }
            case SOUTH: {
                return location.clone().add(1.0, 0.0, 0.0);
            }
            case EAST: {
                return location.clone().add(0.0, 0.0, -1.0);
            }
            case WEST: {
                return location.clone().add(0.0, 0.0, 1.0);
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        this.scan();
        return this.currentLocation != -1;
    }
    
    @Override
    public Location next() {
        this.scan();
        if (this.currentLocation <= -1) {
            throw new NoSuchElementException();
        }
        return this.locationQueue[this.currentLocation--];
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("[LocationIterator] doesn't support location removal");
    }
    
    private void scan() {
        if (this.currentLocation >= 0) {
            return;
        }
        if (this.currentDistance > this.maxDistanceInt) {
            this.end = true;
            return;
        }
        if (this.end) {
            return;
        }
        ++this.currentDistance;
        this.secondError += this.secondStep;
        this.thirdError += this.thirdStep;
        if (this.secondError > 0 && this.thirdError > 0) {
            this.locationQueue[2] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
            if (this.secondStep * this.thirdError < this.thirdStep * this.secondError) {
                this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[2], this.secondFace);
                this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.thirdFace);
            }
            else {
                this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[2], this.thirdFace);
                this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.secondFace);
            }
            this.thirdError -= 16777216;
            this.secondError -= 16777216;
            this.currentLocation = 2;
        }
        else if (this.secondError > 0) {
            this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
            this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.secondFace);
            this.secondError -= 16777216;
            this.currentLocation = 1;
        }
        else if (this.thirdError > 0) {
            this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
            this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.thirdFace);
            this.thirdError -= 16777216;
            this.currentLocation = 1;
        }
        else {
            this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
            this.currentLocation = 0;
        }
    }
}
