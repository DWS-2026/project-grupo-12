package es.codeurjc.web.dto;

import es.codeurjc.web.model.Image;

public record ImageDTO( //class to transfer only the image id, we don't want to expose the image file in the API
	Long id
) {
	ImageDTO(Image image) {
		this(image.getId());
	}
}