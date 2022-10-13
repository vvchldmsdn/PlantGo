package com.ssafy.plantgo.model.service;

import com.ssafy.plantgo.model.dto.PlantDto;
import com.ssafy.plantgo.model.dto.PlantResponse;
import com.ssafy.plantgo.model.entity.PhotoCard;
import com.ssafy.plantgo.model.entity.Plant;
import com.ssafy.plantgo.model.entity.User;
import com.ssafy.plantgo.model.repository.PhotocardRepository;
import com.ssafy.plantgo.model.repository.PlantRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PlantServiceImpl implements PlantService{

    private final PlantRepository plantRepository;
    private final PhotocardRepository photocardRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlantServiceImpl(PlantRepository plantRepository, PhotocardRepository photocardRepository, ModelMapper modelMapper) {
        this.plantRepository = plantRepository;
        this.photocardRepository = photocardRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PlantResponse plantList(Long userSeq, int page) {
        Set<Integer> plantIds = getPlantIds(userSeq);

        Pageable pageable = PageRequest.of(page-1, 10, Sort.by("korName"));
        Page<Plant> result = plantRepository.findAll(pageable);

        List<PlantDto> plantDtoList = new ArrayList<>();
        if(result.getSize()>0){
            result.get().forEach(plant -> {
                PlantDto plantDto =modelMapper.map(plant, PlantDto.class);
                log.info("[plantListService]: plantDto {}", plantDto.toString());
                log.info("[plantListService] plnatIds size {}", plantIds.size());
                if(plantIds.contains(plantDto.getPlantId())) plantDto.setCollected(true);
                plantDtoList.add(plantDto);
            });
        }

        PlantResponse plantResponseDto = PlantResponse.builder()
                .plantDtoList(plantDtoList)
                .totalPage(result.getTotalPages())
                .totalCnt(result.getTotalElements())
                .size(plantIds.size())
                .build();

        return plantResponseDto;
    }

    @Override
    public PlantResponse plantCollected(Long userSeq, int page) {
        Set<Integer> plantIds = getPlantIds(userSeq);

        Pageable pageable = PageRequest.of(page-1, 10, Sort.by("korName"));
        Page<Plant> result = plantRepository.findByPlantIdIn(plantIds, pageable);

        List<PlantDto> plantDtoList = new ArrayList<>();

        result.get().forEach(plant -> {
            PlantDto plantDto =modelMapper.map(plant, PlantDto.class);
            plantDto.setCollected(true);
            plantDtoList.add(plantDto);
        });

        PlantResponse plantResponseDto = PlantResponse.builder()
                .plantDtoList(plantDtoList)
                .totalPage(result.getTotalPages())
                .totalCnt(result.getTotalElements())
                .size(plantIds.size())
                .build();

        return plantResponseDto;
    }

    @Override
    public PlantResponse plantNotCollected(Long userSeq, int page) {
        Set<Integer> plantIds =  getPlantIds(userSeq);

        Pageable pageable = PageRequest.of(page-1, 10, Sort.by("korName"));
        Page<Plant> result = plantRepository.findByPlantIdNotIn(plantIds, pageable);

        List<PlantDto> plantDtoList = new ArrayList<>();
        result.get().forEach(plant -> plantDtoList.add(modelMapper.map(plant, PlantDto.class)));

        PlantResponse plantResponseDto = PlantResponse.builder()
                .plantDtoList(plantDtoList)
                .totalPage(result.getTotalPages())
                .totalCnt(result.getTotalElements())
                .build();

        return plantResponseDto;
    }

    @Override
    public Set<Integer> getPlantIds(Long userSeq) {
        User user = User.builder().userSeq(userSeq).build();
        Set<Integer> plantIds = new HashSet<>();

        Optional<List<PhotoCard>> result = photocardRepository.findByUser(user);
        result.orElseThrow(() -> new NoSuchElementException());

        for(PhotoCard card: result.get()){
            plantIds.add(card.getPlantId());
        }

        return plantIds;
    }
}
