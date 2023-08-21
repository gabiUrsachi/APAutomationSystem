package org.example.services;

import org.example.Roles;
import org.example.customexceptions.InvalidRoleException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthorisationService {
    public Set<Roles> authorize(Set<Roles> receivedRoles, Roles... validRoles){
        Set<Roles> receivedRolesCopy = new HashSet<>(receivedRoles);
        receivedRolesCopy.retainAll(List.of(validRoles));

        if(receivedRolesCopy.size() == 0){
            throw new InvalidRoleException();
        }

        return receivedRolesCopy;
    }
}
