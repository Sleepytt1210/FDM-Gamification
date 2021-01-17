package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.ThumbnailRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Thumbnail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ThumbnailService {
    @Autowired
    private ThumbnailRepository thumbnailRepo;

    @Autowired
    private ChallengeService cls;

    /**
     * Insert and persist data into Thumbnail Table with properties and foreign key ID.
     *
     * @param challengeId  Foreign key id of challenge to be added to.
     * @param base64String Base64String data url of thumbnail.
     * @param fileName     Base file name of thumbnail.
     * @param fileType     File type of thumbnail.
     * @return Thumbnail: Thumbnail entity persisted in database.
     */
    public Thumbnail create(Integer challengeId, String base64String, String fileName, String fileType) {
        Thumbnail thumbnail = new Thumbnail(base64String, fileName, fileType);
        return create(challengeId, thumbnail);
    }

    /**
     * Insert and persist data into Thumbnail Table with thumbnail entity and foreign key ID.
     *
     * @param challengeId Foreign key id of challenge to be added to.
     * @param thumbnail      Thumbnail entity with properties.
     * @return Thumbnail: Thumbnail entity persisted in database.
     * @throws IllegalArgumentException If thumbnail value is not between 1 and 5.
     */
    public Thumbnail create(Integer challengeId, Thumbnail thumbnail) {
        Challenge challenge = cls.findById(challengeId);
        return create(challenge, thumbnail);
    }

    /**
     * Insert and persist data into Thumbnail Table with thumbnail entity and foreign entity.
     *
     * @param challenge Foreign entity challenge to be added to.
     * @param thumbnail    Thumbnail entity with properties.
     * @return Thumbnail: Thumbnail entity persisted in database.
     * @throws IllegalArgumentException If thumbnail value is not between 1 and 5.
     */
    public Thumbnail create(Challenge challenge, Thumbnail thumbnail) throws IllegalArgumentException {
        thumbnail.setChallenge(challenge);
        return thumbnailRepo.saveAndFlush(thumbnail);
    }

    /**
     * Find thumbnail by its ID.
     *
     * @param id Id of thumbnail.
     * @return Thumbnail: Thumbnail entity if found:
     * @throws EntityNotFoundException If thumbnail is not found.
     */
    public Thumbnail findById(Integer id) {
        return thumbnailRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Thumbnail not found!"));
    }

    /**
     * Get all thumbnails in the database.
     *
     * @return List<Thumbnail>: All thumbnails in the database.
     */
    public List<Thumbnail> getAll() {
        return thumbnailRepo.findAll();
    }

    /**
     * Update existing thumbnail in database with its ID.
     *
     * @param thumbnailId     Id of thumbnail to be updated.
     * @param base64String    New base64String url of thumbnail.
     * @param filename        New filename of thumbnail.
     * @param fileType        New fileType of thumbnail.
     * @return Thumbnail: Updated thumbnail entity.
     */
    public Thumbnail update(Integer thumbnailId, String base64String, String filename, String fileType) {
        Thumbnail tempNew = new Thumbnail(base64String, filename, fileType);
        return update(thumbnailId, tempNew);
    }

    /**
     * Update existing thumbnail in database with its ID.
     *
     * @param thumbnailId     Id of thumbnail to be updated.
     * @param newThumbnail    Thumbnail entity with updated value.
     * @return Thumbnail: Updated thumbnail entity.
     */
    public Thumbnail update(Integer thumbnailId, Thumbnail newThumbnail) {
        Thumbnail oldThumbnail = findById(thumbnailId);
        if (newThumbnail.getBase64String() != null) oldThumbnail.setBase64String(newThumbnail.getBase64String());
        if (newThumbnail.getFileName() != null) oldThumbnail.setFileName(newThumbnail.getFileName());
        if (newThumbnail.getFileType() != null) oldThumbnail.setFileType(newThumbnail.getFileType());
        return thumbnailRepo.saveAndFlush(newThumbnail);
    }

    /**
     * Delete a thumbnail by its ID.
     *
     * @param thumbnailId Id of thumbnail to be deleted.
     */
    public void delete(Integer thumbnailId) {
        delete(findById(thumbnailId));
    }

    /**
     * Delete a thumbnail by its entity.
     *
     * @param thumbnail Thumbnail entity to be deleted.
     */
    public void delete(Thumbnail thumbnail) {
        thumbnailRepo.delete(thumbnail);
    }

}
