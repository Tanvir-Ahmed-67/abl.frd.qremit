package abl.frd.qremit.converter.nafex.helper;

import org.springframework.data.jpa.repository.JpaRepository;

public class RepositoryModelWrapper<T>  {
    private final JpaRepository<T, ?> repository;
    //private final Object repository;
    private final Class<T> modelClass;

    public RepositoryModelWrapper(JpaRepository<T, ?> repository, Class<T> modelClass) {
        this.repository = repository;
        this.modelClass = modelClass;
    }

    public JpaRepository<T, ?> getRepository() {
        return repository;
    }

    public Class<T> getModelClass() {
        return modelClass;
    }
}
