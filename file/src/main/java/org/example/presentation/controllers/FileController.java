package org.example.presentation.controllers;

import org.example.business.services.FileService;
import org.example.utils.AuthorizationMapper;
import org.example.utils.data.JwtClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(path = "/{fileIdentifier}", produces = "application/pdf")
    public Resource getFile(@PathVariable String fileIdentifier, HttpServletRequest request) {
        logger.info("[GET request] -> get file by identifier: {}", fileIdentifier);

        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);
        String fileBucket = jwtClaims.getCompanyUUID().toString();

        return this.fileService.getFile(fileBucket, fileIdentifier);
    }
}
