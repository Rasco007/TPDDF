package Domain.Entidades;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
@Getter
@Setter
@Entity
@Table
public class Entidad_Prestadora {
  @Id
  @GeneratedValue
  private int id;
  @Column(columnDefinition = "varchar2(100)")
  private String nombre;
  @ManyToMany
  @JoinTable(name = "entidad_prestadora_entidad",joinColumns = @JoinColumn(name = "entidad_prestadora_id",referencedColumnName = "id"),
                        inverseJoinColumns = @JoinColumn(name = "entidad-id",referencedColumnName = "id"))
  private List<Entidad> entidades;
}