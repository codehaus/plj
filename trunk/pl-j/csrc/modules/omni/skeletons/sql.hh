// This file is generated by omniidl (C++ backend)- omniORB_4_0. Do not edit.
#ifndef __sql_hh__
#define __sql_hh__

#ifndef USE_omniORB_logStream
#define USE_omniORB_logStream
#endif

#ifndef __CORBA_H_EXTERNAL_GUARD__
#include <omniORB4/CORBA.h>
#endif

#ifndef  USE_core_stub_in_nt_dll
# define USE_core_stub_in_nt_dll_NOT_DEFINED_sql
#endif
#ifndef  USE_dyn_stub_in_nt_dll
# define USE_dyn_stub_in_nt_dll_NOT_DEFINED_sql
#endif



#ifndef __commons_hh_EXTERNAL_GUARD__
#define __commons_hh_EXTERNAL_GUARD__
#include <commons.hh>
#endif


#ifdef USE_stub_in_nt_dll
#ifndef USE_core_stub_in_nt_dll
#define USE_core_stub_in_nt_dll
#endif
#ifndef USE_dyn_stub_in_nt_dll
#define USE_dyn_stub_in_nt_dll
#endif
#endif

#ifdef _core_attr
# error "A local CPP macro _core_attr has already been defined."
#else
# ifdef  USE_core_stub_in_nt_dll
#  define _core_attr _OMNIORB_NTDLL_IMPORT
# else
#  define _core_attr
# endif
#endif

#ifdef _dyn_attr
# error "A local CPP macro _dyn_attr has already been defined."
#else
# ifdef  USE_dyn_stub_in_nt_dll
#  define _dyn_attr _OMNIORB_NTDLL_IMPORT
# else
#  define _dyn_attr
# endif
#endif





_CORBA_MODULE org

_CORBA_MODULE_BEG

  _CORBA_MODULE pgj

  _CORBA_MODULE_BEG

    _CORBA_MODULE corba

    _CORBA_MODULE_BEG

      struct prepared_sql {
        typedef _CORBA_ConstrType_Variable_Var<prepared_sql> _var_type;

        
        CORBA::String_member statement_id;

        typedef _CORBA_Unbounded_Sequence< type_value_pair >  _data_seq;
        _data_seq data;

      

        void operator>>= (cdrStream &) const;
        void operator<<= (cdrStream &);
      };

      typedef prepared_sql::_var_type prepared_sql_var;

      typedef _CORBA_ConstrType_Variable_OUT_arg< prepared_sql,prepared_sql_var > prepared_sql_out;

      struct simple_sql {
        typedef _CORBA_ConstrType_Variable_Var<simple_sql> _var_type;

        
        CORBA::String_member statement;

      

        void operator>>= (cdrStream &) const;
        void operator<<= (cdrStream &);
      };

      typedef simple_sql::_var_type simple_sql_var;

      typedef _CORBA_ConstrType_Variable_OUT_arg< simple_sql,simple_sql_var > simple_sql_out;

      struct prepare {
        typedef _CORBA_ConstrType_Variable_Var<prepare> _var_type;

        
        CORBA::String_member statement;

      

        void operator>>= (cdrStream &) const;
        void operator<<= (cdrStream &);
      };

      typedef prepare::_var_type prepare_var;

      typedef _CORBA_ConstrType_Variable_OUT_arg< prepare,prepare_var > prepare_out;

      struct cursor_close {
        typedef _CORBA_ConstrType_Variable_Var<cursor_close> _var_type;

        
        CORBA::String_member cursorname;

      

        void operator>>= (cdrStream &) const;
        void operator<<= (cdrStream &);
      };

      typedef cursor_close::_var_type cursor_close_var;

      typedef _CORBA_ConstrType_Variable_OUT_arg< cursor_close,cursor_close_var > cursor_close_out;

      struct cursor_open_prep {
        typedef _CORBA_ConstrType_Variable_Var<cursor_open_prep> _var_type;

        
        CORBA::String_member cursorname;

        prepared_sql prepared;

      

        void operator>>= (cdrStream &) const;
        void operator<<= (cdrStream &);
      };

      typedef cursor_open_prep::_var_type cursor_open_prep_var;

      typedef _CORBA_ConstrType_Variable_OUT_arg< cursor_open_prep,cursor_open_prep_var > cursor_open_prep_out;

      struct cursor_open_sql {
        typedef _CORBA_ConstrType_Variable_Var<cursor_open_sql> _var_type;

        
        CORBA::String_member cursorname;

        simple_sql sql;

      

        void operator>>= (cdrStream &) const;
        void operator<<= (cdrStream &);
      };

      typedef cursor_open_sql::_var_type cursor_open_sql_var;

      typedef _CORBA_ConstrType_Variable_OUT_arg< cursor_open_sql,cursor_open_sql_var > cursor_open_sql_out;

      enum fetch_direction_type { FETCH_FORWARD, FETCH_BACKWARD /*, __max_fetch_direction_type=0xffffffff */ };
      typedef fetch_direction_type& fetch_direction_type_out;

      struct cursor_fetch {
        typedef _CORBA_ConstrType_Variable_Var<cursor_fetch> _var_type;

        
        CORBA::String_member cursorname;

        fetch_direction_type fetch_direction;

        CORBA::Long count;

      

        void operator>>= (cdrStream &) const;
        void operator<<= (cdrStream &);
      };

      typedef cursor_fetch::_var_type cursor_fetch_var;

      typedef _CORBA_ConstrType_Variable_OUT_arg< cursor_fetch,cursor_fetch_var > cursor_fetch_out;

    _CORBA_MODULE_END

  _CORBA_MODULE_END

_CORBA_MODULE_END



_CORBA_MODULE POA_org
_CORBA_MODULE_BEG

  _CORBA_MODULE pgj
  _CORBA_MODULE_BEG

    _CORBA_MODULE corba
    _CORBA_MODULE_BEG

    _CORBA_MODULE_END

  _CORBA_MODULE_END

_CORBA_MODULE_END





#undef _core_attr
#undef _dyn_attr

inline void operator >>=(org::pgj::corba::fetch_direction_type _e, cdrStream& s) {
  ::operator>>=((CORBA::ULong)_e, s);
}

inline void operator <<= (org::pgj::corba::fetch_direction_type& _e, cdrStream& s) {
  CORBA::ULong _0RL_e;
  ::operator<<=(_0RL_e,s);
  switch (_0RL_e) {
    case org::pgj::corba::FETCH_FORWARD:

    case org::pgj::corba::FETCH_BACKWARD:


    _e = (org::pgj::corba::fetch_direction_type) _0RL_e;
    break;
  default:
    OMNIORB_THROW(MARSHAL,_OMNI_NS(MARSHAL_InvalidEnumValue),
                  (CORBA::CompletionStatus)s.completion());
  }
}





#ifdef   USE_core_stub_in_nt_dll_NOT_DEFINED_sql
# undef  USE_core_stub_in_nt_dll
# undef  USE_core_stub_in_nt_dll_NOT_DEFINED_sql
#endif
#ifdef   USE_dyn_stub_in_nt_dll_NOT_DEFINED_sql
# undef  USE_dyn_stub_in_nt_dll
# undef  USE_dyn_stub_in_nt_dll_NOT_DEFINED_sql
#endif

#endif  // __sql_hh__
