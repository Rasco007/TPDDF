package Domain.Incidente;
import Domain.GeneradorDeRankings.Generador_Rankings;
import Domain.Personas.Comunidad;
import Domain.Personas.Perfil;
import Domain.Notificaciones.Notificador;
import Domain.Notificaciones.Tipos_Notificaciones.Notificacion;
import Domain.Notificaciones.Tipos_Notificaciones.Notificacion_Builder;
import Domain.Notificaciones.Tipos_Notificaciones.Notificacion_Data;
import Domain.Notificaciones.Tipos_Notificaciones.Notificacion_Data_Cierre;
import Domain.Personas.Comunidad;
import Domain.Personas.Usuario;
import Domain.Servicio.Servicio;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table
public class Incidente {
    public Incidente() {
        fecha_hora_de_inicio= new Timestamp(System.currentTimeMillis());
        resuelto=false;
    }

    @Id
    @GeneratedValue
    private int id;
    @OneToOne //todo, no seria many to one asi el servicio puede tener mas de un incidente?
                //aunque pueda tener uno solo no resuelto, el resto los tenes q guardar igual
    @JoinColumn(name = "servicioAfectado_id",referencedColumnName = "id")
    private Servicio servicio_afectado;
    @Column
    private Timestamp fecha_hora_de_inicio;
    @Column
    private Timestamp Fecha_Hora_de_cierre;
    @Column(columnDefinition = "varchar(100)")

    private String observaciones;
    @Column
    private Boolean resuelto;
    @ManyToOne
    @JoinColumn(name = "comunidadAfectada_id",referencedColumnName = "id")
    private Comunidad comunidad_afectada;

    @ManyToOne
    @JoinColumn(name = "usuario_iniciador",referencedColumnName = "id")
    private Perfil usuario_iniciador;
    @ManyToOne
    @JoinColumn(name = "usuario_finalizador",referencedColumnName = "id")
    private Perfil usuario_finalizador;


    //persistencia
    @ManyToOne
    @JoinColumn(name = "generador_semanal",referencedColumnName = "id")
    private Generador_Rankings generadorSemanal;
    private Notificacion_Builder notificacionAbrir = new Notificacion_Data();
    private Notificacion_Builder notificacionCerrar = new Notificacion_Data_Cierre();
    public Incidente(Servicio servicio_afectado, String observaciones, Comunidad comunidad_afectada){
        this.servicio_afectado = servicio_afectado;
        Long datetime = System.currentTimeMillis();
        this.fecha_hora_de_inicio = new Timestamp(datetime);
        this.Fecha_Hora_de_cierre = null;
        this.observaciones = observaciones;
        this.resuelto = false;
        this.comunidad_afectada = comunidad_afectada;
    }

    public void cerrar_incidente(List<Usuario> miembros_a_notificar) throws MessagingException, IOException {
        this.setResuelto(false);
        Long datetime = System.currentTimeMillis();
        this.setFecha_Hora_de_cierre(new Timestamp(datetime));

        Notificacion data = notificacionCerrar.agregar_usuarios_a_notificar(miembros_a_notificar)
            .agregar_mensaje(this).construir();
        Notificador.instancia().notificar(data);
    }
    public void crear_incidente(List<Usuario> miembros_notificar) throws MessagingException, IOException {
        Notificacion data = notificacionAbrir.agregar_usuarios_a_notificar(miembros_notificar)
            .agregar_mensaje(this).construir();
        Notificador.instancia().notificar(data);
    }
}

