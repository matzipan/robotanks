<?php
class Application_Form_LoginForm extends Zend_Form
{  public function init($option = null) 
      { //parent::_constuct($option);
	    $this ->setName('login');
		 $username = new Zend_Form_Element_Text('username');
		 $username ->setLabel('User  name: ')
		                    ->setRequired();
		$password= new Zend_Form_Element_Password('password');
		$password->setLabel('Password:')
		                    ->setRequired(true);
		$login=new Zend_Form_Element_Submit('login');
		$login->setLabel('Login');
		$this ->addElements(array($username, $password, $login));
		$this->setMethod('post');
		$this->setAction('/Authentication/login');
		
		
		}
		}
		?>