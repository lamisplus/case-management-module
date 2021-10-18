import React, {useState, useEffect} from 'react';
import { Modal, ModalHeader, ModalBody,Form,FormFeedback,
Row,Col,FormGroup,Label,Input, Card,CardBody} from 'reactstrap';
import MatButton from '@material-ui/core/Button'
import { makeStyles } from '@material-ui/core/styles'
import SaveIcon from '@material-ui/icons/Save'
import CancelIcon from '@material-ui/icons/Cancel'
import { DateTimePicker } from 'react-widgets';
import "react-widgets/dist/css/react-widgets.css";
import Moment from 'moment';
import momentLocalizer from 'react-widgets-moment';
import moment from "moment";
import {url} from './../../../api'
import { Alert } from 'reactstrap';
import { Spinner } from 'reactstrap';
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";

Moment.locale('en');
momentLocalizer();


const useStyles = makeStyles(theme => ({
    card: {
        margin: theme.spacing(20),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(3)
    },
    submit: {
        margin: theme.spacing(3, 0, 2)
    },
    cardBottom: {
        marginBottom: 20
    },
    Select: {
        height: 45,
        width: 350
    },
    button: {
        margin: theme.spacing(1)
    },

    root: {
        '& > *': {
            margin: theme.spacing(1)
        }
    },
    input: {
        display: 'none'
    } 
}))
Moment.locale('en');
momentLocalizer();


const AssignCaseManager = (props) => {
    const classes = useStyles()
    const totalPatients = props.totalPatients && props.totalPatients !==null ? props.totalPatients : {};
    const manifestPatients= Object.values(totalPatients);
    const [loading, setLoading] = useState(false);
    const [otherfields, setOtherFields] = useState({dateAssign:"",userId:""});
    const [errors, setErrors] = useState({});
    const [caseManager, setCaseManager] = useState([]);
    const [patientManifest, setPatientManifest] = useState({patientIds: [], userId:""})


    useEffect(() => {
        async function getCharacters() {
            try {
                const response = await axios(
                    url + "users/roles/8"
                );
                const body = response.data && response.data !==null ? response.data : {};
                setCaseManager(
                     body.map(({ firstName, lastName, id }) => ({ title: firstName + " " + lastName, value: id }))
                 );
            } catch (error) {
            }
        }
        getCharacters();
    }, []);

    const handleOtherFieldInputChange = e => {
        setOtherFields ({ ...otherfields, [e.target.name]: e.target.value });
    }
    const validate = () => {
        let temp = { ...errors }
        temp.dateAssign = otherfields.dateAssign ? "" : "Date is required"
        temp.userId = otherfields.userId ? "" : "Case Manager Name  is required."

        setErrors({
            ...temp
            })    
        return Object.values(temp).every(x => x == "")
  }

    const saveSample = e => {
      setLoading(true)
      e.preventDefault()
            const patientIds = []
            const modifyManifestPatients= manifestPatients.map(item => {                                    
                patientIds.push(item['patientId']);
                return item;
            })
            //Manipulating the manifestPatients            
                if(validate()){
                 
                    //Process the Patients to be assign 
                    patientManifest['patientIds'] = patientIds;
                    patientManifest['userId'] = otherfields.userId;
                    patientManifest['programCode'] = "0d31f6ee-571c-45b8-80d5-3f7e1d5377b7";
                    
                    console.log(patientManifest)
                    //SENDING A POST REQUEST 
                    axios.post(`${url}application_user_patient/assign`, patientManifest)
                    .then(response => {
                        setLoading(false)
                        toast.success("Patients Assign Successfully!");
                        props.loadPatients();
                         //Closing of the modal 
                        props.togglestatus();
                    })
                    .catch(error => {
                        setLoading(false)
                    });
                    

          }
  }


      
  return (      
      <div >
    <ToastContainer autoClose={3000} hideProgressBar />
            <Modal isOpen={props.modalstatus} toggle={props.togglestatus} className={props.className} size="md">
            <Form onSubmit={saveSample}>
            <ModalHeader toggle={props.togglestatus}>Assign Case Manager</ModalHeader>
                <ModalBody>
                    
                        <Card >
                            <CardBody>
                                <Row >
                                    <Col md={12} >
                                        <Alert color="dark" style={{backgroundColor:'#9F9FA5', color:"#000" , fontWeight: 'bolder', fontSize:'14px'}}>
                                            <p style={{marginTop: '.7rem' }}>
                                                Total Patients  : &nbsp;&nbsp;&nbsp;<span style={{ fontWeight: 'bolder'}}>{Object.keys(totalPatients).length }</span>
                                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                
                                            </p>

                                        </Alert>
                                    </Col>
                                    
                                    <Col md={12}>
                                        {/* <p>Sample Type {manifestSamples.data.description}  </p> */}
                                          <FormGroup>
                                              <Label >Date Assign</Label>
                                          
                                                  <DateTimePicker
                                                      time={false}
                                                      name="dateAssign"
                                                      id="dateAssign"
                                                      onChange={date_transfered =>
                                                        setOtherFields({ ...otherfields, dateAssign: moment(date_transfered).format("YYYY-MM_DD") })
                                                      }
                                                      required/>
                                                      {errors.dateAssign !="" ? (
                                                          <span className={classes.error}>{errors.dateAssign}</span>
                                                      ) : "" }
                                          </FormGroup>
                                      </Col>
                                     
                                      <Col md={12}>
                                          <FormGroup>
                                              <Label for="">Case Managers </Label>
                                              <Input type="select" name="userId" id="userId" 
                                                    vaule={otherfields.userId}
                                                    onChange={handleOtherFieldInputChange}
                                                    {...(errors.userId && { invalid: true})}
                                                  >
                                                        <option> </option>
                                                        {caseManager.map(({ title, value }) => (
                                                            
                                                            <option key={value} value={value}>
                                                                {title}
                                                            </option>
                                                        ))}
                                                  </Input>
                                                      <FormFeedback>{errors.userId}</FormFeedback>
                                          </FormGroup>
                                      </Col>
                                      
                                  </Row>
                                      <br/>
                                      {loading ? <Spinner /> : ""}
                                      <br/>
                                      
                                          <MatButton
                                              type='submit'
                                              variant='contained'
                                              color='primary'
                                              className={classes.button}
                                              startIcon={<SaveIcon />}
                                              disabled={loading}
                                          >   
                                              Save
                                          </MatButton>
                                           
                                          <MatButton
                                              variant='contained'
                                              color='default'
                                              onClick={props.togglestatus}
                                              className={classes.button}
                                              startIcon={<CancelIcon />}
                                          >
                                              Cancel
                                          </MatButton>
                            </CardBody>
                        </Card> 
                    </ModalBody>
        
                </Form>
      </Modal>
    </div>
  );
}

export default AssignCaseManager;
